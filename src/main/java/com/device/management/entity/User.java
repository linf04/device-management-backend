package com.device.management.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ユーザエンティティ（usersテーブル）
 */
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", length = 50)
    private String userId; // ユーザID（プライマリキー）

    @Column(name = "dept_id", length = 50, nullable = false)
    private String deptId; // 部署番号

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 氏名

    @Column(name = "password", length = 255, nullable = false)
    private String password; // パスワード（暗号化保存）

    @CreationTimestamp
    @Column(name = "create_time")
    private LocalDateTime createTime; // 作成日時

    @Column(name = "creater", length = 100)
    private String creater; // 作成者

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime; // 更新日時

    @Column(name = "updater", length = 100)
    private String updater; // 更新者

    // ============= 关联关系 =============

    // 用户类型（字典关联）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id", referencedColumnName = "dict_id", insertable = false, updatable = false)
    private Dict userType;

    @Column(name = "user_type_id")
    private Long userTypeId; // 用户类型ID（辞書項目：USER_TYPE 関連）

    // 用户拥有的设备（一对多）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Device> devices = new ArrayList<>();

    // 用户的采样检查记录（一对多）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SamplingCheck> samplingChecks = new ArrayList<>();

    // ============= 构造函数 =============

    public User() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    public User(String userId, String name, String deptId, Long userTypeId, String password) {
        this.userId = userId;
        this.name = name;
        this.deptId = deptId;
        this.userTypeId = userTypeId;
        this.password = password;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }


}