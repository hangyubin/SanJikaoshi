package com.smartexam.backend.config;

import com.smartexam.backend.service.impl.UserDetailsServiceImpl;
import com.smartexam.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 允许跨域请求
            .cors().and()
            // 禁用CSRF保护，因为我们使用JWT
            .csrf().disable()
            // 配置会话管理为无状态
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置请求授权规则
            .authorizeRequests()
            // 允许所有用户访问的路径
            .antMatchers("/**/login", "/**/register", "/**/refresh-token").permitAll()
            // 允许所有GET请求
            .antMatchers("GET", "/**").permitAll()
            // 其他所有请求需要认证
            .anyRequest().authenticated()
            .and()
            // 禁用默认的登录表单
            .formLogin().disable()
            // 禁用默认的注销功能
            .logout().disable()
            // 禁用HTTP基本认证
            .httpBasic().disable()
            // 添加JWT认证过滤器
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            // 使用自定义的UserDetailsService
            .userDetailsService(userDetailsService)
            // 使用BCryptPasswordEncoder加密密码
            .passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}