package com.device.management.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "device-management");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/api/ping")
    public String ping() {
        return "pong - " + System.currentTimeMillis();
    }
}