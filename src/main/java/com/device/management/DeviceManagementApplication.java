package com.device.management;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * デバイス管理システムSpring Bootマスターアプリケーション
 */
@SpringBootApplication
public class DeviceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceManagementApplication.class, args);
    }

}
