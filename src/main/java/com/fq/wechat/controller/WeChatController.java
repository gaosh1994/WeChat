package com.fq.wechat.controller;

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
        OP_CONTENT_MAP.put("on", "云家居即将点亮LaunchPad小灯!");
        OP_CONTENT_MAP.put("off", "云家居即将关闭LaunchPad小灯!");
        OP_CONTENT_MAP.put("LightOn", "云家居即将打开您的台灯!");
        OP_CONTENT_MAP.put("LightOff", "云家居即将关闭您的台灯!");
        OP_CONTENT_MAP.put("Manual", new StringBuilder("********用户指南********")
                .append("on--------点亮板上小灯")
                .append("off--------关闭板上小灯")
                .append("台灯+开----打开家中台灯")
                .append("台灯+关----关闭家中台灯")
                .append("温度-------获得当前室内温度")
                .append("灯+状态----获得当前台灯/小灯状态")
                .toString());


        QR_CONTENT_MAP.put("temperature", "当前室温为%s℃");
        QR_CONTENT_MAP.put("on", "LaunchPad小灯已打开");
        QR_CONTENT_MAP.put("off", "LaunchPad小灯已关闭");
        QR_CONTENT_MAP.put("LightOn", "台灯已打开");
        QR_CONTENT_MAP.put("LightOff", "台灯已关闭");
    }

    @Autowired
    private WeChatService weService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private FollowerService fService;

    @RequestMapping(value = "/index.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DocumentException {
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
            // 拿到微信文本消息
            String wxMsg = root.element("Content").getText();
            LOGGER.info("--> weixin msg: {} ", wxMsg);

            // 操作小灯/台灯
            if (OP_CONTENT_MAP.containsKey(wxMsg)) {
                content = OP_CONTENT_MAP.get(wxMsg);
                weService.saveStatus(wxMsg);
            } else if (wxMsg.contains("台灯")) {
                if (wxMsg.contains("开")) {
                    wxMsg = "LightOn";
                    content = OP_CONTENT_MAP.get(wxMsg);
                    weService.saveStatus(wxMsg);
                } else if (wxMsg.contains("关")) {
                    wxMsg = "LightOff";
                    content = OP_CONTENT_MAP.get(wxMsg);
                    weService.saveStatus(wxMsg);
                }
            }
            // 获得温度
            else if (wxMsg.contains("温度")) {
                content = String.format(QR_CONTENT_MAP.get("temperature"), sensorService.getSensorContent().getTemperature());
            }
            // 获得台灯/小灯状态
            else if (wxMsg.contains("状态") && wxMsg.contains("灯")) {
                String status = weService.getStatus();
                content = QR_CONTENT_MAP.get(status);
            }
        }

        if (Strings.isNullOrEmpty(content)) {
            content = OP_CONTENT_MAP.get("Manual");
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
}
