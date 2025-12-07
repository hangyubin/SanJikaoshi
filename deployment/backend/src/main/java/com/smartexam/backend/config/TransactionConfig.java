package com.smartexam.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 事务管理配置
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    // Spring Boot 2.x默认已经配置了事务管理器，这里只需要启用事务管理即可
    // 如果需要自定义事务管理器，可以在这里配置
}