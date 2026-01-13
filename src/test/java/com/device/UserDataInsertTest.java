package com.device;

import com.device.management.DeviceManagementApplication;
import com.device.management.repository.UserRepository;
import com.device.management.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

@SpringBootTest(classes = DeviceManagementApplication.class)
public class UserDataInsertTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void insertTestUsers() {
        // 插入测试用户
        User testUser = User.builder()
                .userId("USER001")
                .deptId("D001")
                .name("user")
                .userTypeId(12L)
                .password(passwordEncoder.encode("HYRON@JS2026")) // 加密明文密码
                .createTime(LocalDateTime.now())
                .creater("admin")
                .updateTime(LocalDateTime.now())
                .updater("admin")
                .build();
        userRepository.save(testUser);

        User adminUser = User.builder()
                .userId("ADMIN001")
                .deptId("D000")
                .name("admin")
                .userTypeId(11L)
                .password(passwordEncoder.encode("HYRON@JS2026"))
                .createTime(LocalDateTime.now())
                .creater("system")
                .updateTime(LocalDateTime.now())
                .updater("system")
                .build();
        userRepository.save(adminUser);
    }
}