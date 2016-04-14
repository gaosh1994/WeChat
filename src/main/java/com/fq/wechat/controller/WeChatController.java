package com.fq.wechat.controller;

import com.fq.wechat.constant.CommonConstant;
import com.fq.wechat.service.FollowerService;
import com.fq.wechat.service.SensorService;
import com.fq.wechat.service.WeChatService;
import com.google.common.base.Strings;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jifang.
 * @since 2016/4/3 11:40.
 */
@Controller
@RequestMapping("/wechat")
public class WeChatController {

    private Logger LOGGER = LoggerFactory.getLogger(WeChatController.class);

    private static final Map<String, String> OP_CONTENT_MAP = new HashMap<>();
    private static final Map<String, String> QR_CONTENT_MAP = new HashMap<>();

    private static final String formatter = new StringBuilder("<xml>\n")
            .append("\t\t\t\t<ToUserName><![CDATA[%s]]></ToUserName>\n")
            .append("\t\t\t\t<FromUserName><![CDATA[%s]]></FromUserName>\n")
            .append("\t\t\t\t<CreateTime>%s</CreateTime>\n")
            .append("\t\t\t\t<MsgType><![CDATA[text]]></MsgType>\n")
            .append("\t\t\t\t<Content><![CDATA[%s]]></Content>\n")
            .append("</xml>")
            .toString();

    static {
        OP_CONTENT_MAP.put(CommonConstant.LIGHT_ON, "云家居即将打开您的台灯!");
        OP_CONTENT_MAP.put(CommonConstant.LIGHT_OFF, "云家居即将关闭您的台灯!");
        OP_CONTENT_MAP.put(CommonConstant.MANUAL, new StringBuilder("********用户指南********")
                .append("台灯+开----打开家中台灯")
                .append("台灯+关----关闭家中台灯")
                .append("温度-------获得当前室内温度")
                .append("灯+状态----获得当前台灯/小灯状态")
                .toString());

        QR_CONTENT_MAP.put(CommonConstant.TEMPERATURE, "当前室温为%s℃");
        QR_CONTENT_MAP.put(CommonConstant.LIGHT_ON, "台灯已打开");
        QR_CONTENT_MAP.put(CommonConstant.LIGHT_OFF, "台灯已关闭");
    }

    @Autowired
    private WeChatService weService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private FollowerService fService;

    @RequestMapping(value = "/index.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DocumentException {
        // 将微信消息解析成Dom树
        Element root = parseDomRoot(request);

        String content = null;
        String msgType = root.element("MsgType").getText();

        // 事件消息
        if (msgType.equals("event")) {
            String event = root.element("Event").getText();
            // 关注
            if (event.equals("subscribe")) {
                String follower = root.element("FromUserName").getText();
                content = doSubscribe(follower);
            }
            // 取消关注
            else if (event.equals("unsubscribe")) {
                String follower = root.element("FromUserName").getText();
                content = doUnSubscribe(follower);
            }
        }
        // 文本消息
        else if (msgType.equals("text")) {
            content = doOpAndQr(root.element("Content").getText());
        }
        // 语音消息
        else if (msgType.equals("voice")) {
            content = doOpAndQr(root.element("Recognition").getText());
        }

        if (Strings.isNullOrEmpty(content)) {
            content = OP_CONTENT_MAP.get(CommonConstant.MANUAL);
        }

        // 组装返回值
        String rspMsg = String.format(formatter,
                root.element("FromUserName").getText(),
                root.element("ToUserName").getText(),
                System.currentTimeMillis(),
                content);
        response.setContentType("text/xml;charset=UTF-8");
        response.getWriter().print(rspMsg);
    }

    private Element parseDomRoot(HttpServletRequest request) throws IOException, DocumentException {
        String xml = request.getParameter("xml");
        if (Strings.isNullOrEmpty(xml)) {
            //  String wxMsg = CharStreams.toString(new InputStreamReader(request.getInputStream()));
            //  LOGGER.error("#### --> voice{}", wxMsg);
            return new SAXReader().read(request.getInputStream()).getRootElement();
        } else {
            return new SAXReader().read(new StringReader(xml)).getRootElement();
        }
    }

    private String doSubscribe(String follower) {
        fService.addFollower(follower);
        return OP_CONTENT_MAP.get("Manual");
    }

    private String doUnSubscribe(String follower) {
        fService.removeFollower(follower);
        return "";
    }

    private String doOpAndQr(String wxMsg) {
        LOGGER.info("--> WeChat Message: {} ", wxMsg);

        String content = null;
        // 操作台灯
        if (wxMsg.contains("台灯")) {
            if (wxMsg.contains("开")) {
                weService.saveStatus(CommonConstant.LIGHT_ON);
                content = OP_CONTENT_MAP.get(CommonConstant.LIGHT_ON);
            } else if (wxMsg.contains("关")) {
                weService.saveStatus(CommonConstant.LIGHT_OFF);
                content = OP_CONTENT_MAP.get(CommonConstant.LIGHT_OFF);
            }
        }
        // 获得温度
        else if (wxMsg.contains("温度")) {
            content = String.format(
                    QR_CONTENT_MAP.get(CommonConstant.TEMPERATURE),
                    sensorService.getSensorContent().getTemperature()
            );
        }
        // 获得台灯状态
        else if (wxMsg.contains("状态")) {
            content = QR_CONTENT_MAP.get(weService.getStatus());
        }

        return content;
    }
}
