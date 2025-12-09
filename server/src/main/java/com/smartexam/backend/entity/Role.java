package com.smartexam.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_role")
@EqualsAndHashCode(exclude = {"users", "menus"}) // 排除延迟加载的集合字段
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String code;
    private String description;
    
    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
    
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_role_menu",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id"))
    private Set<Menu> menus;
    
    private Integer status;
    private Long createTime;
    private Long updateTime;
}