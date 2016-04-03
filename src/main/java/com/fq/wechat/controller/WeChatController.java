package com.fq.wechat.controller;

import com.fq.wechat.service.WeChatService;
import com.google.common.base.Strings;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
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
public class WeChatController {

    private Logger LOGGER = LoggerFactory.getLogger(WeChatController.class);

    private static final Map<String, String> contentMap = new HashMap<String, String>();

    static {
        contentMap.put("on", "云家居即将点亮LaunchPad小灯!");
        contentMap.put("off", "云家居即将关闭LaunchPad小灯!");
        contentMap.put("LightOn", "云家居即将打开您的台灯!");
        contentMap.put("LightOff", "云家居即将关闭您的台灯!");
        contentMap.put("NoKey", new StringBuilder("********用户指南********\\n")
                .append("on--------点亮板上小灯\\n")
                .append("off--------关闭板上小灯\\n")
                .append("LightOn----打开家中台灯\\n")
                .append("LightOff----关闭家中台灯")
                .toString());
    }

    @Autowired
    private WeChatService service;

    @RequestMapping(value = "/wechat.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DocumentException {
        Element root;
        String xml = request.getParameter("xml");
        if (Strings.isNullOrEmpty(xml)) {
            root = new SAXReader().read(request.getInputStream()).getRootElement();
        } else {
            root = new SAXReader().read(new StringReader(xml)).getRootElement();
        }


        String wxMsg = root.element("Content").getText();
        LOGGER.info("--> weixin msg: {} ", wxMsg);
        String content;
        if (contentMap.containsKey(wxMsg)) {
            content = contentMap.get(wxMsg);
            service.saveStatus(wxMsg);
        } else {
            content = contentMap.get("NoKey");
        }

        // 组装返回值

        Element resultRoot = DocumentHelper.createElement("xml");
        resultRoot.addElement("ToUserName").setText(root.element("FromUserName").getText());
        resultRoot.addElement("FromUserName").setText(root.element("ToUserName").getText());
        resultRoot.addElement("CreateTime").setText(root.element("CreateTime").getText());
        resultRoot.addElement("MsgType").setText(root.element("MsgType").getText());
        resultRoot.addElement("Content").setText(content);
        String result = DocumentHelper.createDocument(resultRoot).asXML();
        response.getOutputStream().print(result);
    }
}
