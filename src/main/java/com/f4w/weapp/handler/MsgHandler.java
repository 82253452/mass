package com.f4w.weapp.handler;

import com.f4w.dto.BusiQuestionDto;
import com.f4w.entity.BusiApp;
import com.f4w.mapper.BusiAppMapper;
import com.f4w.mapper.BusiQuestionMapper;
import com.f4w.weapp.WxOpenService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.f4w.utils.Constant.REPLAY_REQUESTION;
import static com.f4w.utils.Constant.REPLAY_REQUESTION_GLOABLE;

@Component
@Slf4j
public class MsgHandler implements WxMpMessageHandler {

    @Resource
    protected WxOpenService wxOpenService;
    @Resource
    private BusiAppMapper busiAppMapper;
    @Resource
    private BusiQuestionMapper busiQuestionMapper;

    @Override
    public WxMpXmlOutMessage handle(
            WxMpXmlMessage wxMessage,
            Map<String, Object> context,
            WxMpService weixinService,
            WxSessionManager sessionManager) throws WxErrorException {
        BusiApp busiApp = new BusiApp();
        busiApp.setAppId(weixinService.getWxMpConfigStorage().getAppId());
        busiApp = busiAppMapper.selectOne(busiApp);
        List<BusiQuestionDto> list = null;
        String render = "暂时未上传，请留言课程名称，或添加QQ171947004，私聊呦";
        if (REPLAY_REQUESTION == busiApp.getReplay()) {
            list = busiQuestionMapper.getOneListQuestion(wxMessage.getContent(), busiApp.getUid(), busiApp.getAppId());
        } else if (REPLAY_REQUESTION_GLOABLE == busiApp.getReplay()) {
            list = busiQuestionMapper.getOneListQuestion(wxMessage.getContent(), busiApp.getUid(), null);
        } else {
            render = "没有回复，请联系管理员后台配置";
        }
        if (CollectionUtils.isNotEmpty(list)) {
            render = buildQuestion(list);
        }
        if (render.getBytes().length >= 2048) {
            render = "返回内容过多，请换个关键词试试！";
        }
        return WxMpXmlOutMessage.TEXT().content(render)
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build();
    }

    private String buildQuestion(List<BusiQuestionDto> list) {
        final String[] out = {""};
        list.forEach(m -> {
            out[0] += m.toString();
        });
        return out[0];
    }

}
