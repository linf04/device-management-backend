package com.device.management.repository;

import com.device.management.dto.DeviceUsagePermissionDTO;
import com.device.management.entity.DeviceUsagePermission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * デバイス使用権限リポジトリインターフェース
 */
@Repository
@Transactional
public class DeviceUsagePermissionRepositoryImpl {
    /** 権限詳細取得 */
    @PersistenceContext
    private EntityManager entityManager;
    public Optional<DeviceUsagePermissionDTO> findPermissionDetailCustom(String permissionId) {
        String jpql = "SELECT " +
                "p.permissionId, " +
                "d.deviceId, d.computerName, d.deviceModel, " +
                "d.project, d.devRoom, " +
                "u.userId, u.name, u.deptId, " +
                "ds.dictItemName, p.domainGroup, p.noDomainReason, " +
                "ss.dictItemName, p.noSmartitReason, " +
                "us.dictItemName, p.usbReason, p.usbExpireDate, " +
                "avs.dictItemName, p.noSymantecReason, " +
                "p.remark, p.createTime, p.creater, " +
                "p.updateTime, p.updater " +
                "FROM DeviceUsagePermission p " +
                "LEFT JOIN p.device d " +
                "LEFT JOIN d.user u " +
                "LEFT JOIN p.domainStatus ds " +
                "LEFT JOIN p.smartitStatus ss " +
                "LEFT JOIN p.usbStatus us " +
                "LEFT JOIN p.antivirusStatus avs " +
                "WHERE p.permissionId = :permissionId";

        try {
            Object[] result = (Object[]) entityManager.createQuery(jpql)
                    .setParameter("permissionId", permissionId)
                    .getSingleResult();

            if (result != null) {
                DeviceUsagePermissionDTO dto = new DeviceUsagePermissionDTO();
                dto.setPermissionId((String) result[0]);           // permissionId
                dto.setDeviceId((String) result[1]);               // deviceId
                dto.setComputerName((String) result[2]);           // computerName
                dto.setDeviceModel((String) result[3]);            // deviceModel
                dto.setProject((String) result[4]);                // project
                dto.setDevRoom((String) result[5]);                // devRoom
                dto.setUserId((String) result[6]);                 // userId
                dto.setUserName((String) result[7]);               // userName
                dto.setDeptId((String) result[8]);                 // deptId
                dto.setDomainStatus((String) result[9]);           // domainStatus
                dto.setDomainGroup((String) result[10]);           // domainGroup
                dto.setNoDomainReason((String) result[11]);        // noDomainReason
                dto.setSmartitStatus((String) result[12]);         // smartitStatus
                dto.setNoSmartitReason((String) result[13]);       // noSmartitReason
                dto.setUsbStatus((String) result[14]);             // usbStatus
                dto.setUsbReason((String) result[15]);             // usbReason
                if (result[16] != null) {
                    dto.setUsbExpireDate(((LocalDate) result[16])); // usbExpireDate
                }
                dto.setAntivirusStatus((String) result[17]);       // antivirusStatus
                dto.setNoSymantecReason((String) result[18]);      // noSymantecReason
                dto.setRemark((String) result[19]);                // remark
                dto.setCreateTime((LocalDateTime) result[20]);     // createTime
                dto.setCreater((String) result[21]);               // creater
                dto.setUpdateTime((LocalDateTime) result[22]);     // updateTime
                dto.setUpdater((String) result[23]);               // updater

                return Optional.of(dto);
            }
        } catch (Exception e) {
            // レコードが存在しない場合
            return Optional.empty();
        }

        return Optional.empty();
    }

    /** 权限信息获取（包含关联实体） */
    public Optional<DeviceUsagePermission> findPermissionWithDetailsCriteria(String permissionId, boolean includeDevice, boolean includeUser) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DeviceUsagePermission> query = cb.createQuery(DeviceUsagePermission.class);
        Root<DeviceUsagePermission> root = query.from(DeviceUsagePermission.class);

        if (includeDevice) {
            root.fetch("device", JoinType.LEFT);
        }

        root.fetch("domainStatus", JoinType.LEFT);
        root.fetch("smartitStatus", JoinType.LEFT);
        root.fetch("usbStatus", JoinType.LEFT);
        root.fetch("antivirusStatus", JoinType.LEFT);

        query.where(cb.equal(root.get("permissionId"), permissionId));

        try {
            DeviceUsagePermission result = entityManager.createQuery(query).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /** デバイスIDに基づいて権限情報を検索 */
    public Optional<DeviceUsagePermission> findByDeviceId(String deviceId) {
        String jpql = "SELECT p FROM DeviceUsagePermission p WHERE p.device.deviceId = :deviceId";
        try {
            DeviceUsagePermission result = entityManager.createQuery(jpql, DeviceUsagePermission.class)
                    .setParameter("deviceId", deviceId)
                    .getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** 権限情報の更新 */
    @Transactional
    public boolean updatePermission(String permissionId, DeviceUsagePermission updatedData) {
        try {

            DeviceUsagePermission existing = entityManager.find(DeviceUsagePermission.class, permissionId);
            if (existing == null) {
                return false;
            }

            if (updatedData.getDomainStatus() != null) {
                existing.setDomainStatus(updatedData.getDomainStatus());
            }
            if (updatedData.getDomainGroup() != null) {
                existing.setDomainGroup(updatedData.getDomainGroup());
            }
            if (updatedData.getNoDomainReason() != null) {
                existing.setNoDomainReason(updatedData.getNoDomainReason());
            }

            if (updatedData.getSmartitStatus() != null) {
                existing.setSmartitStatus(updatedData.getSmartitStatus());
            }
            if (updatedData.getNoSmartitReason() != null) {
                existing.setNoSmartitReason(updatedData.getNoSmartitReason());
            }

            if (updatedData.getUsbStatus() != null) {
                existing.setUsbStatus(updatedData.getUsbStatus());
            }
            if (updatedData.getUsbReason() != null) {
                existing.setUsbReason(updatedData.getUsbReason());
            }
            if (updatedData.getUsbExpireDate() != null) {
                existing.setUsbExpireDate(updatedData.getUsbExpireDate());
            }

            if (updatedData.getAntivirusStatus() != null) {
                existing.setAntivirusStatus(updatedData.getAntivirusStatus());
            }
            if (updatedData.getNoSymantecReason() != null) {
                existing.setNoSymantecReason(updatedData.getNoSymantecReason());
            }

            if (updatedData.getRemark() != null) {
                existing.setRemark(updatedData.getRemark());
            }

            if (updatedData.getUpdater() != null) {
                existing.setUpdater(updatedData.getUpdater());
            }

            entityManager.merge(existing);
            entityManager.flush();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
