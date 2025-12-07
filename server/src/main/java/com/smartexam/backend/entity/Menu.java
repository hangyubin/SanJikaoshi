package com.smartexam.backend.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer orderNum;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Menu parent;
    
    @OneToMany(mappedBy = "parent")
    private Set<Menu> children;
    
    @ManyToMany(mappedBy = "menus")
    private Set<Role> roles;
    
    private Integer status;
    private Long createTime;
    private Long updateTime;
}