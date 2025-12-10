package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单数据访问接口
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    /**
     * 根据菜单路径查询菜单
     * @param path 菜单路径
     * @return 菜单对象
     */
    Menu findByPath(String path);
    
    /**
     * 根据父ID查询菜单列表
     * @param parentId 父菜单ID
     * @return 菜单列表
     */
    List<Menu> findByParentId(Long parentId);
    
    /**
     * 根据状态查询菜单列表
     * @param status 菜单状态
     * @return 菜单列表
     */
    List<Menu> findByStatus(Integer status);
}


