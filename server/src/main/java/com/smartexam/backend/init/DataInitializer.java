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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

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

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始执行数据初始化...");
        
        // 1. 检查数据库连接和表结构
        checkDatabaseConnection();
        checkTableStructure();
        
        // 2. 初始化基础数据
        initAdminRole();
        initStudentRole();
        initAdminUser();
        initDefaultSubjects();
        
        System.out.println("数据初始化完成！");
    }

    /**
     * 检查数据库连接是否正常
     */
    private void checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("数据库连接成功：" + connection.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("数据库连接失败：" + e.getMessage());
            throw new RuntimeException("数据库连接失败，无法初始化数据", e);
        }
    }

    /**
     * 检查表结构是否完整
     */
    private void checkTableStructure() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // 检查必要的表是否存在
            String[] tableNames = {"sys_role", "sys_user", "sys_user_role", "exam_subject"};
            for (String tableName : tableNames) {
                ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
                if (resultSet.next()) {
                    System.out.println("表 " + tableName + " 已存在");
                } else {
                    System.out.println("表 " + tableName + " 不存在，Hibernate将自动创建");
                }
            }
        } catch (SQLException e) {
            System.err.println("检查表结构失败：" + e.getMessage());
            throw new RuntimeException("检查表结构失败，无法初始化数据", e);
        }
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
     * 初始化学员角色
     */
    private void initStudentRole() {
        Role studentRole = roleRepository.findByCode("ROLE_STUDENT");
        if (studentRole == null) {
            studentRole = new Role();
            studentRole.setName("学员");
            studentRole.setCode("ROLE_STUDENT");
            studentRole.setDescription("普通学员角色，拥有学习和考试权限");
            studentRole.setStatus(1);
            studentRole.setCreateTime(System.currentTimeMillis());
            studentRole.setUpdateTime(System.currentTimeMillis());
            roleRepository.save(studentRole);
            System.out.println("学员角色初始化完成");
        } else {
            System.out.println("学员角色已存在");
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
        
        // 检查默认科目是否完整
        long currentTime = System.currentTimeMillis();
        int addedCount = 0;
        
        // 检查并添加基本理论科目
        if (existingSubjects.stream().noneMatch(subject -> "BASIC_THEORY".equals(subject.getCode()))) {
            Subject basicTheory = new Subject();
            basicTheory.setName("基本理论");
            basicTheory.setDescription("医疗卫生系统三基考试-基本理论部分");
            basicTheory.setCode("BASIC_THEORY");
            basicTheory.setStatus(1);
            basicTheory.setCreateTime(currentTime);
            basicTheory.setUpdateTime(currentTime);
            subjectRepository.save(basicTheory);
            addedCount++;
            System.out.println("添加基本理论科目");
        }
        
        // 检查并添加基础知识科目
        if (existingSubjects.stream().noneMatch(subject -> "BASIC_KNOWLEDGE".equals(subject.getCode()))) {
            Subject basicKnowledge = new Subject();
            basicKnowledge.setName("基础知识");
            basicKnowledge.setDescription("医疗卫生系统三基考试-基础知识部分");
            basicKnowledge.setCode("BASIC_KNOWLEDGE");
            basicKnowledge.setStatus(1);
            basicKnowledge.setCreateTime(currentTime);
            basicKnowledge.setUpdateTime(currentTime);
            subjectRepository.save(basicKnowledge);
            addedCount++;
            System.out.println("添加基础知识科目");
        }
        
        // 检查并添加基本技能科目
        if (existingSubjects.stream().noneMatch(subject -> "BASIC_SKILLS".equals(subject.getCode()))) {
            Subject basicSkills = new Subject();
            basicSkills.setName("基本技能");
            basicSkills.setDescription("医疗卫生系统三基考试-基本技能部分");
            basicSkills.setCode("BASIC_SKILLS");
            basicSkills.setStatus(1);
            basicSkills.setCreateTime(currentTime);
            basicSkills.setUpdateTime(currentTime);
            subjectRepository.save(basicSkills);
            addedCount++;
            System.out.println("添加基本技能科目");
        }
        
        if (addedCount > 0) {
            System.out.println("默认科目初始化完成，新增" + addedCount + "个科目");
        } else {
            System.out.println("默认科目已完整，共" + existingSubjects.size() + "个科目");
        }
    }
}


