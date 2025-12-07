package com.smartexam.backend.config;

import com.smartexam.backend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取请求头中的Authorization字段
        String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String token = null;
        
        // 检查Authorization头是否存在，并且格式是否正确
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            token = authorizationHeader.substring(7);
            // 从JWT令牌中获取用户名
            username = jwtUtils.getUsernameFromToken(token);
        }
        
        // 如果用户名不为空，且当前SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 验证JWT令牌是否有效
            if (jwtUtils.validateToken(token, userDetails.getUsername())) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // 设置认证详情
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 将认证令牌存储到SecurityContext中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}