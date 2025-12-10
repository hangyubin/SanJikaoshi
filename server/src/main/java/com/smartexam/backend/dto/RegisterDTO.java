package com.smartexam.backend.dto;

import lombok.Data;

/**
 * 注册请求DTO，用于接收前端发送的注册数据
 */
@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String realName;
    private String phone;
    private Integer gender;
    private Long departmentId;
    private String jobTitle;
}