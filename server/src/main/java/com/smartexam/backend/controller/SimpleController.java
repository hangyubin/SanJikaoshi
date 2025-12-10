package com.smartexam.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单控制器，用于测试基本的API请求是否能正常工作
 */
@RestController
public class SimpleController {

    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello, World!");
        response.put("status", "success");
        return response;
    }

    @PostMapping("/api/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Echo received");
        response.put("data", data);
        response.put("status", "success");
        return response;
    }
}