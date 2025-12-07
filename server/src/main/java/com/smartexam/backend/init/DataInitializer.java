package com.smartexam.backend.init;

import com.smartexam.backend.entity.Role;
import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.RoleRepository;
import com.smartexam.backend.repository.UserRepository;
import com.smartexam.backend.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

/**
 * 数据初始化类，用于在应用启动时自动创建管理员账号
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化管理员角色
        initAdminRole();
        
        // 初始化管理员账号
        initAdminUser();
    }

    /**
     * 初始化管理员角色
     */
    private void initAdminRole() {
        Role adminRole = roleRepository.findByCode("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("管理员");
            adminRole.setCode("ROLE_ADMIN");
            adminRole.setDescription("系统管理员角色，拥有所有权限");
            adminRole.setStatus(1);
            adminRole.setCreateTime(System.currentTimeMillis());
            adminRole.setUpdateTime(System.currentTimeMillis());
            roleRepository.save(adminRole);
            System.out.println("管理员角色初始化完成");
        } else {
            System.out.println("管理员角色已存在");
        }
    }

    /**
     * 初始化管理员账号
     */
    private void initAdminUser() {
        Optional<User> optionalUser = userRepository.findByUsername("admin");
        if (!optionalUser.isPresent()) {
            // 创建管理员账号
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(PasswordUtils.encodePassword("admin123"));
            adminUser.setRealName("系统管理员");
            adminUser.setEmail("admin@example.com");
            adminUser.setPhone("13800138000");
            adminUser.setStatus(1);
            adminUser.setCreateTime(System.currentTimeMillis());
            adminUser.setUpdateTime(System.currentTimeMillis());

            // 分配管理员角色
            Role adminRole = roleRepository.findByCode("ROLE_ADMIN");
            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            // 保存管理员账号
            userRepository.save(adminUser);
            System.out.println("管理员账号初始化完成，用户名：admin，密码：admin123");
        } else {
            System.out.println("管理员账号已存在");
        }
    }
}