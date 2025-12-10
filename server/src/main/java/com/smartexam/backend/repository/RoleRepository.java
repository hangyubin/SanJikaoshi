package com.smartexam.backend.repository;

import com.smartexam.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 角色数据访问接口
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色代码查询角色
     * @param code 角色代码
     * @return 角色对象
     */
    Role findByCode(String code);
    
    /**
     * 根据状态查询角色列表
     * @param status 角色状态
     * @return 角色列表
     */
    List<Role> findByStatus(Integer status);
}


