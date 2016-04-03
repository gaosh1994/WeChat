package com.fq.wechat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author jifang.
 * @since 2016/4/3 11:40.
 */
@Controller
public class Test {

    private Logger LOGGER = LoggerFactory.getLogger(Test.class);
    private String Token = "feiqing";

    @RequestMapping(value = "/connect.do", method = {RequestMethod.POST, RequestMethod.GET})
    public void connect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Enumeration<String> keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = request.getParameter(key);
            LOGGER.error(key + " -> " + value);
        }

        LOGGER.error("HTTP_RAW_POST_DATA header: " + request.getHeader("HTTP_RAW_POST_DATA"));

        LOGGER.error("HTTP_RAW_POST_DATA content: " + request.getParameter("HTTP_RAW_POST_DATA"));

        String[] values = request.getParameterValues("HTTP_RAW_POST_DATA");
        if (values != null){
            LOGGER.error("---------------");
            for (String value : values){
                LOGGER.error(value);
            }
            LOGGER.error("--------------");
        }


        String echostr = request.getParameter("echostr");
        if (echostr != null && !"".equals(echostr)) {
            response.getWriter().print(echostr);//完成相互认证
        }
    }
}
