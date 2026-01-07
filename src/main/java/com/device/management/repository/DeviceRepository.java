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
