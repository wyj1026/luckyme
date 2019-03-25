package com.tegongdete.luckyme.interceptor;

import com.tegongdete.luckyme.plugin.limitter.RequestRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class RequestRateLimitInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimitInterceptor.class);

    RequestRateLimiter requestNumLimitter;

    @Autowired
    public void setRequestNumLimitter(ApplicationContext context, @Value("${useRedisLimiter}") boolean useRedisLimiter) {
        if (useRedisLimiter) {
            logger.info("Using redis rate limiter!");
            requestNumLimitter = (RequestRateLimiter) context.getBean("redisRateLimiter");
        }
        else {
            logger.info("Using guava rate limiter!");
            requestNumLimitter = (RequestRateLimiter) context.getBean("tokenBucketRateLimiter");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (requestNumLimitter == null) {
            throw new NullPointerException("requestRateLimitter is null");
        }
        if (!requestNumLimitter.isAccessible()) {
            logger.warn("Max Request Limit Reached!!!!");
            response.sendRedirect("/finished");
            return false;
        }
        return true;
    }

}
