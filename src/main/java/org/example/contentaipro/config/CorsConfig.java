package org.example.contentaipro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)//允许浏览器把 Cookie 带过去
                .allowedOriginPatterns("*")
                .allowedMethods("get", "post", "put", "delete", "head", "options")
        /*每个方法什么意思：
        方法	含义
        GET	查数据
        POST	创建
        PUT	更新
        DELETE	删除*/
                .allowedHeaders("*").exposedHeaders("*");//允许所有请求头和响应头
    }
}
