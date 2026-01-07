package com.device.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    private String userId; // 従業員番号

    @Column(name = "dept_id")
    private String deptId; // 部門

    @Column(name = "name")
    private String name; // 氏名

    @Column(name = "user_type_id")
    private Long userTypeId; // ユーザータイプID（dictテーブルと関連）

    @Column(name = "password")
    private String password; // 暗号化後のパスワード

    @Column(name = "create_time")
    private LocalDateTime createTime; // 作成時間

    @Column(name = "creater")
    private String creater; // 作成者

    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新時間

    @Column(name = "updater")
    private String updater; // 更新者
}