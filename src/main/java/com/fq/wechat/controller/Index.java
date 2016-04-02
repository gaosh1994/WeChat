package com.fq.wechat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jifang.
 * @since 2016/4/2 12:51.
 */
@Controller
public class Index {

    private static final Logger LOGGER = LoggerFactory.getLogger(Index.class);

    @ResponseBody
    @RequestMapping("/index.do")
    public void index(HttpServletRequest request){
        LOGGER.error(request.getRemoteHost());
        LOGGER.error("---->>>> index...", new RuntimeException("hh"));
    }
}
