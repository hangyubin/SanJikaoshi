package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}