package com.tegongdete.luckyme.interceptor;

import com.tegongdete.luckyme.plugin.limitter.RequestNumLimitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class RequestNumInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RequestNumInterceptor.class);

    @Autowired
    RequestNumLimitter requestNumLimitter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (requestNumLimitter == null) {
            throw new NullPointerException("requestNumLimitter is null");
        }
        if (!requestNumLimitter.isAccessible()) {
            logger.info("Max Request Limit Reached!!!!");
            response.sendRedirect("/finished");
            return false;
        }
        return true;
    }

}
