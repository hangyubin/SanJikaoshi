package com.smartexam.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器，用于测试API请求是否能正常工作
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<?> testGet() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "GET请求成功");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<?> testPost(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "POST请求成功");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}