package com.fq.wechat.controller;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * @author jifang.
 * @since 2016/4/3 11:40.
 */
@Controller
public class WeChatController {

    private Logger LOGGER = LoggerFactory.getLogger(WeChatController.class);

    @RequestMapping(value = "/wechat.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, DocumentException {

//        Enumeration<String> keys = request.getParameterNames();
//        while (keys.hasMoreElements()) {
//            String key = keys.nextElement();
//            String value = request.getParameter(key);
//            LOGGER.error(key + " -> " + value);
//        }

        Element root;
        String xml = request.getParameter("xml");
        if (Strings.isNullOrEmpty(xml)) {
            root = new SAXReader().read(request.getInputStream()).getRootElement();
        } else {
            root = new SAXReader().read(new StringReader(xml)).getRootElement();
        }

        String content = root.element("Content").getText();
        LOGGER.error(content);
    }
}
