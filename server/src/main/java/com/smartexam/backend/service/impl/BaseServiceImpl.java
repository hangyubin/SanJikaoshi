package com.smartexam.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基础服务实现类，提供事务管理支持
 */
public abstract class BaseServiceImpl {
    
    /**
     * 保存或更新操作，支持事务
     * @param <T> 实体类型
     * @param entity 实体对象
     * @return 保存或更新后的实体
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> T saveOrUpdate(T entity) {
        // 由子类实现具体逻辑
        return entity;
    }
    
    /**
     * 删除操作，支持事务
     * @param <T> 实体类型
     * @param entity 实体对象
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void delete(T entity) {
        // 由子类实现具体逻辑
    }
    
    /**
     * 批量删除操作，支持事务
     * @param <T> 实体类型
     * @param entities 实体对象列表
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void deleteBatch(Iterable<T> entities) {
        // 由子类实现具体逻辑
    }
    
    /**
     * 批量保存操作，支持事务
     * @param <T> 实体类型
     * @param entities 实体对象列表
     * @return 保存后的实体列表
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> Iterable<T> saveBatch(Iterable<T> entities) {
        // 由子类实现具体逻辑
        return entities;
    }
}


