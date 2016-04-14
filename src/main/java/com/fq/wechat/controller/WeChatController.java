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
        OP_CONTENT_MAP.put(CommonConstant.MANUAL, new StringBuilder("您发送的消息是\" %s \"")
                .append("\n")
                .append("***> 用户手册 <***")
                .append("1. 台灯+开 ---- 打开台灯")
                .append("2. 台灯+关 ---- 关闭台灯")
                .append("3. 温度 ------- 室内温度")
                .append("4. 状态 ------- 台灯状态")
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
        String wxMsg = "";
        String msgType = root.element(CommonConstant.MSG_TYPE).getText();

        // 事件消息
        if (msgType.equals(CommonConstant.L_EVENT)) {
            // 获得粉丝用户名
            String follower = root.element(CommonConstant.FROM_USER_NAME).getText();
            String event = root.element(CommonConstant.U_EVENT).getText();
            // 关注
            if (event.equals(CommonConstant.SUBSCRIBE)) {
                content = doSubscribe(follower);
            }
            // 取消关注
            else if (event.equals(CommonConstant.UN_SUBSCRIBE)) {
                content = doUnSubscribe(follower);
            }
        }
        // 文本消息
        else if (msgType.equals(CommonConstant.TEXT)) {
            wxMsg = root.element(CommonConstant.CONTENT).getText();
            content = doOpAndQr(wxMsg);
        }
        // 语音消息
        else if (msgType.equals(CommonConstant.VOICE)) {
            wxMsg = root.element(CommonConstant.RECOGNITION).getText();
            content = doOpAndQr(wxMsg);
        }

        if (Strings.isNullOrEmpty(content)) {
            content = String.format(OP_CONTENT_MAP.get(CommonConstant.MANUAL), wxMsg);
        }

        // response.setContentType("text/xml;charset=UTF-8");
        response.getWriter().print(composeResponseData(root, content));
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
        return OP_CONTENT_MAP.get(CommonConstant.MANUAL);
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

    // 组装返回值
    private String composeResponseData(Element root, String content) {
        return String.format(formatter,
                root.element(CommonConstant.FROM_USER_NAME).getText(),
                root.element(CommonConstant.TO_USER_NAME).getText(),
                System.currentTimeMillis(),
                content);
    }
}
