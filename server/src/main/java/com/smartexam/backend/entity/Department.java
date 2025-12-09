package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String code;
    private String description;
    private Integer status;
    
    @OneToMany(mappedBy = "department")
    private Set<User> users;
    
    private Long createTime;
    private Long updateTime;
}