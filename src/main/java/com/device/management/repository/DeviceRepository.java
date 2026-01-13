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
 * 设备 Repository
 * */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    List<Device> findByDeviceId(String deviceId);

    // 全ての重複しない開発室名を取得
    @Query("SELECT DISTINCT d.devRoom FROM Device d WHERE d.devRoom IS NOT NULL AND TRIM(d.devRoom) != '' ORDER BY d.devRoom")
    List<String> findDistinctDevRooms();

    // 全ての重複しないプロジェクト名を取得
    @Query("SELECT DISTINCT d.project FROM Device d WHERE d.project IS NOT NULL AND TRIM(d.project) != '' ORDER BY d.project")
    List<String> findDistinctProjects();

    // 条件によるユーザーIDリストの検索
    @Query("SELECT DISTINCT d.userId FROM Device d WHERE d.userId IS NOT NULL " +
            "AND (:deviceName IS NULL OR d.computerName LIKE %:deviceName%) " +
            "AND (:userId IS NULL OR d.userId = :userId)")
    List<String> findUserIdsByCondition(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId
    );

    // ユーザーIDリストによるデバイス検索（関連付き）
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE d.userId IN :userIds")
    List<Device> findByUserIdsWithDicts(@Param("userIds") List<String> userIds);

    // デバイスIDによるデバイス検索（関連付き）
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE TRIM(d.deviceId) = :deviceId")
    Device findByDeviceIdWithDicts(@Param("deviceId") String deviceId);

    // 条件によるデバイスのページング検索
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.user u " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE (:deviceName IS NULL OR d.computerName LIKE %:deviceName%) " +
            "AND (:userId IS NULL OR d.userId = :userId) " +
            "AND (:userName IS NULL OR u.userName LIKE %:userName%) " +
            "AND (:project IS NULL OR d.project LIKE %:project%) " +
            "AND (:devRoom IS NULL OR d.devRoom LIKE %:devRoom%)")
    List<Device> findByConditions(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId,
            @Param("userName") String userName,
            @Param("project") String project,
            @Param("devRoom") String devRoom
    );

    // 総レコード数の取得（条件付き）
    @Query("SELECT COUNT(d) FROM Device d " +
            "LEFT JOIN d.user u " +
            "WHERE (:deviceName IS NULL OR d.computerName LIKE %:deviceName%) " +
            "AND (:userId IS NULL OR d.userId = :userId) " +
            "AND (:userName IS NULL OR u.userName LIKE %:userName%) " +
            "AND (:project IS NULL OR d.project LIKE %:project%) " +
            "AND (:devRoom IS NULL OR d.devRoom LIKE %:devRoom%)")
    Long countByConditions(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId,
            @Param("userName") String userName,
            @Param("project") String project,
            @Param("devRoom") String devRoom
    );

    // デバイスIDリストによるデバイスIPマッピングの取得
    @Query("SELECT TRIM(d.device.deviceId) as deviceId, d FROM DeviceIp d WHERE d.device.deviceId IN :deviceIds")
    List<Object[]> findDeviceIpsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    // デバイスIDリストによるモニターマッピングの取得
    @Query("SELECT TRIM(m.device.deviceId) as deviceId, m FROM Monitor m WHERE m.device.deviceId IN :deviceIds")
    List<Object[]> findMonitorsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE device_info SET " +
            "device_model = COALESCE(:deviceModel, device_model), " +
            "computer_name = COALESCE(:computerName, computer_name), " +
            "login_username = COALESCE(:loginUsername, login_username), " +
            "project = COALESCE(:project, project), " +
            "dev_room = COALESCE(:devRoom, dev_room), " +
            "user_id = COALESCE(:userId, user_id), " +
            "remark = COALESCE(:remark, remark), " +
            "self_confirm_id = COALESCE(:selfConfirmId, self_confirm_id), " +
            "os_id = COALESCE(:osId, os_id), " +
            "memory_id = COALESCE(:memoryId, memory_id), " +
            "ssd_id = COALESCE(:ssdId, ssd_id), " +
            "hdd_id = COALESCE(:hddId, hdd_id), " +
            "update_time = COALESCE(:updateTime, update_time), " +
            "updater = COALESCE(:updater, updater) " +
            "WHERE device_id = :deviceId",
            nativeQuery = true)
    int updateDevice(
            @Param("deviceId") String deviceId,
            @Param("deviceModel") String deviceModel,
            @Param("computerName") String computerName,
            @Param("loginUsername") String loginUsername,
            @Param("project") String project,
            @Param("devRoom") String devRoom,
            @Param("userId") String userId,
            @Param("remark") String remark,
            @Param("selfConfirmId") Integer selfConfirmId,
            @Param("osId") Long osId,
            @Param("memoryId") Long memoryId,
            @Param("ssdId") Long ssdId,
            @Param("hddId") Long hddId,
            @Param("updateTime") LocalDateTime updateTime,
            @Param("updater") String updater
    );

}