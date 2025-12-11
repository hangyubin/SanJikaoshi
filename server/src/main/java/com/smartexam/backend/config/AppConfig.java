package com.smartexam.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用配置类
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    
    /**
     * 密码编码器配置
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 静态资源配置，支持前后端一体化部署和Docker外部资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源路径，支持classpath和外部文件系统（用于Docker部署）
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/", 
                        "file:/app/static/",  // 支持Docker外部静态资源
                        "file:/static/"        // 备用外部路径
                )
                .resourceChain(true);
        
        // 显式配置index.html作为静态资源，避免循环视图问题
        registry.addResourceHandler("/index.html")
                .addResourceLocations(
                        "classpath:/static/index.html", 
                        "file:/app/static/index.html",  // 支持Docker外部index.html
                        "file:/static/index.html"        // 备用外部路径
                )
                .resourceChain(true);
    }
}