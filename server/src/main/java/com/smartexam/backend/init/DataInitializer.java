package com.smartexam.backend.init;

import com.smartexam.backend.entity.Role;
import com.smartexam.backend.entity.Subject;
import com.smartexam.backend.entity.User;
import com.smartexam.backend.repository.RoleRepository;
import com.smartexam.backend.repository.SubjectRepository;
import com.smartexam.backend.repository.UserRepository;
import com.smartexam.backend.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * 数据初始化类，用于在应用启动时自动创建管理员账号和默认科目
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Transactional // 添加事务注解，确保在事务中执行
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化管理员角色
        initAdminRole();
        
        // 初始化管理员账号
        initAdminUser();
        
        // 初始化默认科目
        initDefaultSubjects();
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

    /**
     * 初始化默认科目（基本理论、基础知识、基本技能）
     */
    private void initDefaultSubjects() {
        List<Subject> existingSubjects = subjectRepository.findAll();
        if (existingSubjects.isEmpty()) {
            long currentTime = System.currentTimeMillis();

            // 创建基本理论科目
            Subject basicTheory = new Subject();
            basicTheory.setName("基本理论");
            basicTheory.setDescription("医疗卫生系统三基考试-基本理论部分");
            basicTheory.setCode("BASIC_THEORY");
            basicTheory.setStatus(1);
            basicTheory.setCreateTime(currentTime);
            basicTheory.setUpdateTime(currentTime);

            // 创建基础知识科目
            Subject basicKnowledge = new Subject();
            basicKnowledge.setName("基础知识");
            basicKnowledge.setDescription("医疗卫生系统三基考试-基础知识部分");
            basicKnowledge.setCode("BASIC_KNOWLEDGE");
            basicKnowledge.setStatus(1);
            basicKnowledge.setCreateTime(currentTime);
            basicKnowledge.setUpdateTime(currentTime);

            // 创建基本技能科目
            Subject basicSkills = new Subject();
            basicSkills.setName("基本技能");
            basicSkills.setDescription("医疗卫生系统三基考试-基本技能部分");
            basicSkills.setCode("BASIC_SKILLS");
            basicSkills.setStatus(1);
            basicSkills.setCreateTime(currentTime);
            basicSkills.setUpdateTime(currentTime);

            // 保存所有科目
            List<Subject> defaultSubjects = subjectRepository.saveAll(
                    List.of(basicTheory, basicKnowledge, basicSkills)
            );

            System.out.println("默认科目初始化完成：" + defaultSubjects.size() + "个科目");
        } else {
            System.out.println("默认科目已存在，共" + existingSubjects.size() + "个科目");
        }
    }
}