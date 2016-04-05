package com.fq.wechat.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

/**
 * @author jifang.
 * @since 2016/4/5 10:56.
 */
public class AccessFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest hRequest = (HttpServletRequest) request;

        LOGGER.info("host: " + hRequest.getRemoteHost());
        LOGGER.info("--> req: headers");
        Enumeration<String> names = hRequest.getHeaderNames();
        while (names.hasMoreElements()) {
            String header = names.nextElement();
            LOGGER.info(header + " ->" + hRequest.getHeader(header));
        }

        LOGGER.info("--> req: params");
        Enumeration<String> parameters = hRequest.getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameter = parameters.nextElement();
            LOGGER.info(parameter + " -> " + hRequest.getParameter(parameter));
        }

        chain.doFilter(request, response);

        HttpServletResponse hResponse = (HttpServletResponse) response;
        Collection<String> rspNames = hResponse.getHeaderNames();
        if (rspNames != null) {
            LOGGER.info("--> rsp: headers");
            for (String name : rspNames) {
                LOGGER.info(name + " ->" + hResponse.getHeader(name));
            }
        }

        LOGGER.info("character_encoding: " + hResponse.getCharacterEncoding());
    }

    public void destroy() {

    }
}
