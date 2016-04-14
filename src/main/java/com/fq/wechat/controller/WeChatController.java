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

    private static final Map<String, String> contentMap = new HashMap<String, String>();

    static {
        contentMap.put("on", "云家居即将点亮LaunchPad小灯!");
        contentMap.put("off", "云家居即将关闭LaunchPad小灯!");
        contentMap.put("LightOn", "云家居即将打开您的台灯!");
        contentMap.put("LightOff", "云家居即将关闭您的台灯!");
        contentMap.put("temperature", "当前室温为%s℃");
        contentMap.put("NoKey", new StringBuilder("********用户指南********")
                .append("on--------点亮板上小灯")
                .append("off--------关闭板上小灯")
                .append("台灯+开----打开家中台灯")
                .append("台灯+关----关闭家中台灯")
                .toString());
    }

    @Autowired
    private WeChatService weChatService;

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
            // 拿到微信消息组装返回content并插入数据库
            String wxMsg = root.element("Content").getText();
            LOGGER.info("--> weixin msg: {} ", wxMsg);
            if (contentMap.containsKey(wxMsg)) {
                content = contentMap.get(wxMsg);
                weChatService.saveStatus(wxMsg);
            } else if (wxMsg.contains("台灯")) {
                if (wxMsg.contains("开")) {
                    wxMsg = "LightOn";
                    content = contentMap.get(wxMsg);
                    weChatService.saveStatus(wxMsg);
                } else if (wxMsg.contains("关")) {
                    wxMsg = "LightOff";
                    content = contentMap.get(wxMsg);
                    weChatService.saveStatus(wxMsg);
                } else {
                    content = contentMap.get("NoKey");
                }
            } else if (wxMsg.contains("温度")) {
                content = String.format(contentMap.get("temperature"), sensorService.getSensorContent().getTemperature());
            } else {
                content = contentMap.get("NoKey");
            }
        }

        // 组装返回值
        String formatter = new StringBuilder("<xml>\n")
                .append("\t\t\t\t<ToUserName><![CDATA[%s]]></ToUserName>\n")
                .append("\t\t\t\t<FromUserName><![CDATA[%s]]></FromUserName>\n")
                .append("\t\t\t\t<CreateTime>%s</CreateTime>\n")
                .append("\t\t\t\t<MsgType><![CDATA[text]]></MsgType>\n")
                .append("\t\t\t\t<Content><![CDATA[%s]]></Content>\n")
                .append("</xml>")
                .toString();
        String rspMsg = String.format(formatter,
                root.element("FromUserName").getText(),
                root.element("ToUserName").getText(),
                System.currentTimeMillis(),
                content);
        LOGGER.info("--> weixin response: {}", rspMsg);
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
        return contentMap.get("NoKey");
    }

    private String doUnSubscribe(String follower) {
        fService.removeFollower(follower);
        return "";
    }
}
