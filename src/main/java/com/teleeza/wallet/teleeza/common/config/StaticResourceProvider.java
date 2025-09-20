package com.teleeza.wallet.teleeza.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceProvider implements WebMvcConfigurer {
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
//                .addResourceLocations("file:images\\"); // use this when testing on local host
        .addResourceLocations("file:images/"); // Use this when deploying to server
    }
}
