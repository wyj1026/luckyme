package com.tegongdete.luckyme;

import com.tegongdete.luckyme.interceptor.RequestRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Component
public class WebMvcConfig implements WebMvcConfigurer {
    private final RequestRateLimitInterceptor requestNumInterceptor;

    public WebMvcConfig(RequestRateLimitInterceptor requestNumInterceptor) {
        this.requestNumInterceptor = requestNumInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestNumInterceptor).addPathPatterns("/lucky/**");
    }
}
