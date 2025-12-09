package com.smartexam.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"roles", "department"}) // 排除关联字段
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    private String jobTitle;
    private String employeeId;
    private Integer yearsOfService;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    
    private Integer status;
    private Long createTime;
    private Long updateTime;
}