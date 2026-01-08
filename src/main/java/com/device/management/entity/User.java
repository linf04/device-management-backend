package com.device.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users", schema = "public")
public class User{
    @Id
    private String userId;

    private String deptId;
    private String name;
    private String password;
    private Long userTypeId;
    private LocalDateTime createTime;
    private String creater;
    private LocalDateTime updateTime;
    private String updater;
}