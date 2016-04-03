package com.fq.wechat.filter;

import com.google.common.base.Strings;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author jifang.
 * @since 2016/4/3 14:51.
 */
public class WeChatFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String echo = request.getParameter("echostr");
        if (!Strings.isNullOrEmpty(echo)) {
            response.getWriter().print(echo);
        } else{
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
