package com.tegongdete.luckyme;

import com.tegongdete.luckyme.interceptor.RequestNumInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Component
public class WebMvcConfig implements WebMvcConfigurer {
    private final RequestNumInterceptor requestNumInterceptor;

    public WebMvcConfig(RequestNumInterceptor requestNumInterceptor) {
        this.requestNumInterceptor = requestNumInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestNumInterceptor).addPathPatterns("/lucky/**");
    }
}
