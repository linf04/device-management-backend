package com.device.management.repository;

import com.device.management.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/*
 * デバイス Repository
 * */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {



    // デバイス詳細（辞書情報を含む）
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE TRIM(d.deviceId) = :deviceId")
    Device findByDeviceIdWithDicts(@Param("deviceId") String deviceId);

    // デバイスIP情報のバッチ取得
    @Query("SELECT TRIM(d.device.deviceId) as deviceId, d FROM DeviceIp d WHERE d.device.deviceId IN :deviceIds")
    List<Object[]> findDeviceIpsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    // モニター情報のバッチ取得
    @Query("SELECT TRIM(m.device.deviceId) as deviceId, m FROM Monitor m WHERE m.device.deviceId IN :deviceIds")
    List<Object[]> findMonitorsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    boolean existsByDeviceId(String deviceId);

    // 将原有的两个方法替换为：

    @Query("SELECT d FROM Device d " +
            "WHERE (:computerName IS NULL OR d.computerName LIKE %:computerName%) " +
            "AND (:userId IS NULL OR d.userId IS NULL OR d.userId LIKE %:userId%)")
    List<Device> findByComputerNameContainingIgnoreCaseAndUserIdContainingIgnoreCase(
            @Param("computerName") String computerName,
            @Param("userId") String userId
    );

    @Query("SELECT COUNT(d) FROM Device d " +
            "WHERE (:computerName IS NULL OR d.computerName LIKE %:computerName%) " +
            "AND (:userId IS NULL OR d.userId IS NULL OR d.userId LIKE %:userId%)")
    Long countByComputerNameContainingIgnoreCaseAndUserIdContainingIgnoreCase(
            @Param("computerName") String computerName,
            @Param("userId") String userId
    );

    // 获取所有不重复的开发室名称
    @Query("SELECT DISTINCT d.devRoom FROM Device d WHERE d.devRoom IS NOT NULL AND TRIM(d.devRoom) != '' ORDER BY d.devRoom")
    List<String> findDistinctDevRooms();

    // 获取所有不重复的项目名称
    @Query("SELECT DISTINCT d.project FROM Device d WHERE d.project IS NOT NULL AND TRIM(d.project) != '' ORDER BY d.project")
    List<String> findDistinctProjects();

}