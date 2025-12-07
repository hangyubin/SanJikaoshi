package com.smartexam.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt加密，比MD5更安全
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，方便开发测试
            .csrf().disable()
            // 配置请求授权
            .authorizeRequests()
            // 所有请求都允许访问，移除JWT认证
            .anyRequest().permitAll()
            .and()
            // 允许表单登录
            .formLogin().permitAll()
            // 允许注销
            .and().logout().permitAll();
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静态资源放行
        web.ignoring().antMatchers("/static/**", "/favicon.ico");
    }
}