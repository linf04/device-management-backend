import com.device.DeviceManagementApplication;
import com.device.management.repository.DictRepository;
import com.device.management.repository.UserRepository;
import com.device.management.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;

@SpringBootTest(classes = DeviceManagementApplication.class) // 指定扫描的根包
public class UserDataInsertTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DictRepository dictRepository;

    @Test
    void insertTestUsers() {
        // 插入测试用户
        User testUser = User.builder()
                .userId("USER001")
                .deptId("D001")
                .name("user")
                .userType(dictRepository.findById(12L).get())
                .password(passwordEncoder.encode("HYRON@JS2026")) // 加密明文密码
                .createTime(Instant.now())
                .creater("admin")
                .updateTime(Instant.now())
                .updater("admin")
                .build();
        userRepository.save(testUser);

        User adminUser = User.builder()
                .userId("ADMIN001")
                .deptId("D000")
                .name("admin")
                .userType(dictRepository.findById(11L).get())
                .password(passwordEncoder.encode("HYRON@JS2026"))
                .createTime(Instant.now())
                .creater("system")
                .updateTime(Instant.now())
                .updater("system")
                .build();
        userRepository.save(adminUser);

    }
}