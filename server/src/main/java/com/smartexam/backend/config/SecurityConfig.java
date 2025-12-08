package com.smartexam.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用BCrypt加密，比MD5更安全
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，方便开发测试
            .csrf().disable()
            // 配置请求授权
            .authorizeRequests()
            // 公开路径，不需要认证
            .antMatchers("/auth/login", "/auth/register", "/auth/refresh-token").permitAll()
            // 其他所有路径都需要认证
            .anyRequest().authenticated()
            .and()
            // 禁用表单登录，使用JWT认证
            .formLogin().disable()
            // 禁用HTTP基本认证
            .httpBasic().disable()
            // 禁用会话管理，使用无状态认证
            .sessionManagement().disable();
        
        // 添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静态资源和Swagger文档放行
        web.ignoring().antMatchers(
            "/static/**", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**"
        );
    }
}