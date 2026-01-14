package com.device.management.repository;

import com.device.management.entity.Device;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/*
 * デバイス Repository
 * */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    List<Device> findByDeviceId(String deviceId);

    // すべての重複しない開発室名を取得
    @Query("SELECT DISTINCT d.devRoom FROM Device d WHERE d.devRoom IS NOT NULL AND TRIM(d.devRoom) != '' ORDER BY d.devRoom")
    List<String> findDistinctDevRooms();

    // すべての重複しないプロジェクト名を取得
    @Query("SELECT DISTINCT d.project FROM Device d WHERE d.project IS NOT NULL AND TRIM(d.project) != '' ORDER BY d.project")
    List<String> findDistinctProjects();

    // 条件に基づいてユーザーIDリストを検索
    @Query("SELECT DISTINCT d.userId FROM Device d WHERE d.userId IS NOT NULL " +
            "AND (:deviceName IS NULL OR d.computerName LIKE %:deviceName%) " +
            "AND (:userId IS NULL OR d.userId = :userId)")
    List<String> findUserIdsByCondition(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId
    );

    // ユーザーIDリストに基づいてデバイスを検索（関連付け付き）
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE d.userId IN :userIds")
    List<Device> findByUserIdsWithDicts(@Param("userIds") List<String> userIds);

    // デバイスIDに基づいてデバイスを検索（関連付け付き）
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE TRIM(d.deviceId) = :deviceId")
    Device findByDeviceIdWithDicts(@Param("deviceId") String deviceId);

    // 条件に基づいてデバイスをページング検索
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.user u " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE (:deviceName IS NULL OR d.computerName LIKE %:deviceName%) " +
            "AND (:userId IS NULL OR d.userId = :userId) " +
            "AND (:name IS NULL OR u.name LIKE %:name%) " +
            "AND (:project IS NULL OR d.project LIKE %:project%) " +
            "AND (:devRoom IS NULL OR d.devRoom LIKE %:devRoom%)")
    List<Device> findByConditions(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId,
            @Param("name") String name,
            @Param("project") String project,
            @Param("devRoom") String devRoom
    );

    // 総レコード数を取得（条件付き）
    @Query("SELECT COUNT(d) FROM Device d " +
            "LEFT JOIN d.user u " +
            "WHERE (:deviceName IS NULL OR d.computerName LIKE %:deviceName%) " +
            "AND (:userId IS NULL OR d.userId = :userId) " +
            "AND (:name IS NULL OR u.name LIKE %:name%) " +
            "AND (:project IS NULL OR d.project LIKE %:project%) " +
            "AND (:devRoom IS NULL OR d.devRoom LIKE %:devRoom%)")
    Long countByConditions(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId,
            @Param("name") String name,
            @Param("project") String project,
            @Param("devRoom") String devRoom
    );

    // デバイスIDリストに基づいてデバイスIP情報を検索
    @Query("SELECT TRIM(d.device.deviceId) as deviceId, d FROM DeviceIp d WHERE d.device.deviceId IN :deviceIds")
    List<Object[]> findDeviceIpsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    // デバイスIDリストに基づいてモニター情報を検索
    @Query("SELECT TRIM(m.device.deviceId) as deviceId, m FROM Monitor m WHERE m.device.deviceId IN :deviceIds")
    List<Object[]> findMonitorsByDeviceIds(@Param("deviceIds") List<String> deviceIds);
}