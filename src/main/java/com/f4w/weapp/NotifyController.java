package com.f4w.weapp;

import com.f4w.entity.BusiApp;
import com.f4w.mapper.BusiAppMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import me.chanjar.weixin.open.util.WxOpenCryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {
    @Autowired
    protected WxOpenService wxOpenService;
    @Resource
    private BusiAppMapper busiAppMapper;

    @RequestMapping("authorizerRefreshToken")
    public Object authorizerRefreshToken(
            @RequestParam("auth_code") String authCode, @RequestParam("expires_in") String expiresIn) {
        log.info(
                "\n接收微信请求：[authCode=[{}], expiresIn=[{}]",
                authCode, expiresIn);
        try {
            wxOpenService.getWxOpenComponentService().getQueryAuth(authCode);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return "授权成功";
    }

    @RequestMapping("message")
    public Object receiveTicket(@RequestBody(required = false) String requestBody, @RequestParam("timestamp") String timestamp,
                                @RequestParam("nonce") String nonce, @RequestParam("signature") String signature,
                                @RequestParam(name = "encrypt_type", required = false) String encType,
                                @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info(
                "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!StringUtils.equalsIgnoreCase("aes", encType) || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
        String out = null;
        try {
            out = wxOpenService.getWxOpenComponentService().route(inMessage);
        } catch (WxErrorException e) {
            log.error("error");
        }

        log.debug("\n组装回复信息：{}", out);

        return out;
    }

    @RequestMapping("{appId}/callback")
    public Object callback(@RequestBody(required = false) String requestBody,
                           @PathVariable("appId") String appId,
                           @RequestParam("signature") String signature,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("nonce") String nonce,
                           @RequestParam("openid") String openid,
                           @RequestParam("encrypt_type") String encType,
                           @RequestParam("msg_signature") String msgSignature) {
        log.info(
                "\n接收微信请求：[appId=[{}], openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                appId, openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        if (!StringUtils.equalsIgnoreCase("aes", encType) || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        String out = "success";
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(appId);
        busiApp = busiAppMapper.selectOne(busiApp);
        if (null == busiApp) {
            return out;
        }
        log.info("有appid");
        WxOpenXmlMessage.fromEncryptedXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        // aes加密的消息
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.info("解密后");
        log.info("content" + inMessage.getContent());
        log.info("\n消息解密后内容为：\n{} ", inMessage.toString());
        // 全网发布测试用例
        try {
            if (StringUtils.equals(inMessage.getMsgType(), "text")) {
                if (StringUtils.equals(inMessage.getContent(), "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                    out = new WxOpenCryptUtil(wxOpenService.getWxOpenConfigStorage()).encrypt(
                            WxMpXmlOutMessage.TEXT().content("TESTCOMPONENT_MSG_TYPE_TEXT_callback")
                                    .fromUser(inMessage.getToUser())
                                    .toUser(inMessage.getFromUser())
                                    .build()
                                    .toXml()
                    );
                } else if (StringUtils.startsWith(inMessage.getContent(), "QUERY_AUTH_CODE:")) {
                    String msg = inMessage.getContent().replace("QUERY_AUTH_CODE:", "") + "_from_api";
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(msg).toUser(inMessage.getFromUser()).build();
                    wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                }
            } else if (StringUtils.equals(inMessage.getMsgType(), "event")) {
                if (StringUtils.equals(inMessage.getEvent(), "weapp_audit_success")) {
                    busiApp.setStatus(6);
                    busiApp.setAuditMsg("审核通过");
                    busiAppMapper.updateByPrimaryKey(busiApp);
                } else if (StringUtils.equals(inMessage.getEvent(), "weapp_audit_fail")) {
                    busiApp.setStatus(7);
                    busiApp.setAuditMsg(inMessage.getFailReason());
                    busiAppMapper.updateByPrimaryKey(busiApp);
                }
//                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(inMessage.getEvent() + "from_callback").toUser(inMessage.getFromUser()).build();
//                    wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
            }
        } catch (WxErrorException e) {
            log.error("callback", e);
        }
        return out;
    }
}
