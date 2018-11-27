package com.f4w.weapp;

import com.f4w.dto.BusiQuestionDto;
import com.f4w.entity.BusiApp;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.BusiQuestionMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizerInfo;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import me.chanjar.weixin.open.util.WxOpenCryptUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.ListUI;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.f4w.utils.Constant.REPLAY_REQUESTION;
import static com.f4w.utils.Constant.REPLAY_REQUESTION_GLOABLE;

@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {
    @Resource
    protected WxOpenService wxOpenService;
    @Resource
    private BusiAppMapper busiAppMapper;
    @Resource
    private BusiQuestionMapper busiQuestionMapper;

    @RequestMapping("authorizerRefreshToken")
    public void authorizerRefreshToken(
            @RequestParam("auth_code") String authCode
            , @RequestParam("expires_in") String expiresIn
            , @RequestParam("uid") Long uid
            , HttpServletResponse response) throws IOException {
        log.info(
                "\n接收微信请求：[authCode=[{}], expiresIn=[{}], uid=[{}]",
                authCode, expiresIn, uid);
        try {
            WxOpenQueryAuthResult wxOpenQueryAuthResult = wxOpenService.getWxOpenComponentService().getQueryAuth(authCode);
            String appId = wxOpenQueryAuthResult.getAuthorizationInfo().getAuthorizerAppid();
            BusiApp busiApp = new BusiApp();
            busiApp.setAppId(appId);
            busiApp = busiAppMapper.selectOne(busiApp);
            if (null == busiApp) {
                //获取应用信息
                WxOpenAuthorizerInfoResult wxOpenAuthorizerInfoResult = wxOpenService.getWxOpenComponentService().getAuthorizerInfo(appId);
                WxOpenAuthorizerInfo wxOpenAuthorizerInfo = wxOpenAuthorizerInfoResult.getAuthorizerInfo();
                busiApp = new BusiApp();
                busiApp.setAppId(appId);
                busiApp.setNickName(wxOpenAuthorizerInfo.getNickName());
                busiApp.setHeadImg(wxOpenAuthorizerInfo.getHeadImg());
                busiApp.setServiceTypeInfo(wxOpenAuthorizerInfo.getServiceTypeInfo());
                busiApp.setVerifyTypeInfo(wxOpenAuthorizerInfo.getVerifyTypeInfo());
                busiApp.setUserName(wxOpenAuthorizerInfo.getUserName());
                busiApp.setPrincipalName(wxOpenAuthorizerInfo.getPrincipalName());
                busiApp.setQrcodeUrl(wxOpenAuthorizerInfo.getQrcodeUrl());
                busiApp.setSignature(wxOpenAuthorizerInfo.getSignature());
                busiApp.setStatus(1);
                busiApp.setReplay(0);
                busiApp.setUid(uid);
                if (null == wxOpenAuthorizerInfo.getMiniProgramInfo()) {
                    busiApp.setMiniProgramInfo(1);
                } else {
                    busiApp.setMiniProgramInfo(2);
                }
                busiAppMapper.insert(busiApp);
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        response.sendRedirect("/index.html#/busi/busiApp");
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
        WxOpenXmlMessage.fromEncryptedXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        // aes加密的消息
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody, wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.info("\n消息解密后内容为：\n{} ", inMessage.toString());
        // 全网发布测试用例
        String pushOut = buildPust(appId, inMessage);
        if (StringUtils.isNotBlank(pushOut)) {
            log.info("返回测试--{}" + pushOut);
            return pushOut;
        }
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(appId);
        busiApp = busiAppMapper.selectOne(busiApp);
        if (null == busiApp) {
            return out;
        }
        if (StringUtils.equals(inMessage.getMsgType(), "text")) {
            List<BusiQuestionDto> list = null;
            String render = "暂时未上传，请留言课程名称，或添加QQ171947004，私聊呦";
            if (REPLAY_REQUESTION == busiApp.getReplay()) {
                list = busiQuestionMapper.getOneListQuestion(inMessage.getContent(), busiApp.getUid(), busiApp.getAppId());
            } else if (REPLAY_REQUESTION_GLOABLE == busiApp.getReplay()) {
                list = busiQuestionMapper.getOneListQuestion(inMessage.getContent(), busiApp.getUid(), null);
            } else {
                render = "没有回复，请联系管理员后台配置";
            }
            if (CollectionUtils.isNotEmpty(list)) {
                render = buildQuestion(list);
            }
            if (render.getBytes().length >= 2048) {
                render = "返回内容过多，请换个关键词试试！";
            }
            out = new WxOpenCryptUtil(wxOpenService.getWxOpenConfigStorage()).encrypt(
                    WxMpXmlOutMessage.TEXT().content(render)
                            .fromUser(inMessage.getToUser())
                            .toUser(inMessage.getFromUser())
                            .build()
                            .toXml()
            );
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
        }
        log.info("返回的内容为：\n{}" + out);
        return out;
    }

    private String buildQuestion(List<BusiQuestionDto> list) {
        final String[] out = {""};
        list.forEach(m -> {
            out[0] += m.toString();
        });
        return out[0];
    }

    private String buildPust(String appId, WxMpXmlMessage inMessage) {
        String out = "";
        if (StringUtils.equalsAnyIgnoreCase(appId, "wxd101a85aa106f53e", "wx570bc396a51b8ff8")) {
            try {
                log.info("测试content--{}" + inMessage.getContent());
                if (StringUtils.equals(inMessage.getMsgType(), "text")) {
                    if (StringUtils.equals(inMessage.getContent(), "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                        out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(
                                WxMpXmlOutMessage.TEXT().content("TESTCOMPONENT_MSG_TYPE_TEXT_callback")
                                        .fromUser(inMessage.getToUser())
                                        .toUser(inMessage.getFromUser())
                                        .build(),
                                wxOpenService.getWxOpenConfigStorage()
                        );
                    } else if (StringUtils.startsWith(inMessage.getContent(), "QUERY_AUTH_CODE:")) {
                        String msg = inMessage.getContent().replace("QUERY_AUTH_CODE:", "") + "_from_api";
                        WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(msg).toUser(inMessage.getFromUser()).build();
                        wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                    }
                } else if (StringUtils.equals(inMessage.getMsgType(), "event")) {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT().content(inMessage.getEvent() + "from_callback").toUser(inMessage.getFromUser()).build();
                    wxOpenService.getWxOpenComponentService().getWxMpServiceByAppid(appId).getKefuService().sendKefuMessage(kefuMessage);
                    out = "success";
                }
            } catch (WxErrorException e) {
                log.error("callback", e);
            }
        }
        return out;
    }
}
