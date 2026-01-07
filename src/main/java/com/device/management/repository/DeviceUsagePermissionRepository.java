package com.device.management.repository;

import com.device.management.entity.DeviceUsagePermission;
import com.device.management.entity.Dict;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * デバイス使用権限リポジトリインターフェース
 */

public interface DeviceUsagePermissionRepository extends
        JpaRepository<DeviceUsagePermission, String>,
        JpaSpecificationExecutor<DeviceUsagePermission> {
    /** 権限詳細取得 */
    @Query("SELECT p FROM DeviceUsagePermission p " +
            "LEFT JOIN FETCH p.device d " +
            "LEFT JOIN FETCH p.domainStatus " +
            "LEFT JOIN FETCH p.smartitStatus " +
            "LEFT JOIN FETCH p.usbStatus " +
            "LEFT JOIN FETCH p.antivirusStatus " +
            "WHERE p.permissionId = :id")
    Optional<DeviceUsagePermission> findPermissionWithDetails(@Param("id") String id);

    /** 権限情報更新 */
    @Modifying
    @Transactional
    @Query("UPDATE DeviceUsagePermission p SET " +
            "p.domainStatus = :domainStatus, " +
            "p.domainGroup = COALESCE(:domainGroup, p.domainGroup), " +
            "p.noDomainReason = COALESCE(:noDomainReason, p.noDomainReason), " +
            "p.smartitStatus = :smartitStatus, " +
            "p.noSmartitReason = COALESCE(:noSmartitReason, p.noSmartitReason), " +
            "p.usbStatus = :usbStatus, " +
            "p.usbReason = COALESCE(:usbReason, p.usbReason), " +
            "p.usbExpireDate = COALESCE(:usbExpireDate, p.usbExpireDate), " +
            "p.antivirusStatus = :antivirusStatus, " +
            "p.noSymantecReason = COALESCE(:noSymantecReason, p.noSymantecReason), " +
            "p.remark = COALESCE(:remark, p.remark), " +
            "p.updater = COALESCE(:updater, p.updater), " +
            "p.updateTime = CURRENT_TIMESTAMP " +
            "WHERE p.permissionId = :id")
    int updatePermission(
            @Param("id") String id,
            @Param("domainStatus") Dict domainStatus,
            @Param("domainGroup") String domainGroup,
            @Param("noDomainReason") String noDomainReason,
            @Param("smartitStatus") Dict smartitStatus,
            @Param("noSmartitReason") String noSmartitReason,
            @Param("usbStatus") Dict usbStatus,
            @Param("usbReason") String usbReason,
            @Param("usbExpireDate") LocalDate usbExpireDate,
            @Param("antivirusStatus") Dict antivirusStatus,
            @Param("noSymantecReason") String noSymantecReason,
            @Param("remark") String remark,
            @Param("updater") String updater);

}
