package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DevicePermissionVo;
import com.device.management.dto.PermissionsDTO;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import com.device.management.entity.Dict;
import com.device.management.exception.BusinessException;
import com.device.management.repository.DevicePermissionRepository;
import com.device.management.repository.DeviceRepository;
import com.device.management.repository.DictRepository;
import com.device.management.security.JwtTokenProvider;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;


@Service
public class DevicePermissionService {
    @Resource
    DevicePermissionRepository devicePermissionRepository;
    @Resource
    DeviceRepository deviceRepository;
    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private DictRepository dictRepository;

    public DevicePermissionVo getPermissionVoByPermissionId(String permissionId) {
        DevicePermissionVo devicePermissionVo = devicePermissionRepository.findPermissionVoByPermissionId(permissionId);
        if (devicePermissionVo == null) {
            throw new BusinessException(30003, "没有找到该权限信息");
        }
        return devicePermissionVo;
    }

    public Page<DevicePermissionVo> getPermissions(Pageable pageable) {
        return devicePermissionRepository.findAllDevicePermissionVo(pageable);
    }

    public PermissionsDTO addPermissions(PermissionsDTO permissionsDTO) {


        DeviceInfo deviceInfo = deviceRepository.findByDeviceId(permissionsDTO.getDeviceId());
        if (deviceInfo == null) {
            throw new BusinessException(30001, "设备不存在");
        }

        DevicePermission devicePermissions = devicePermissionRepository.findDevicePermissionsByDevice(deviceInfo);

        if (devicePermissions != null) {
            throw new BusinessException(30002, "设备已存在权限信息");
        }

        devicePermissionRepository.save(DevicePermission.builder().permissionId(UUID.randomUUID().toString()).device(deviceInfo).domainStatus(Dict.builder().id(permissionsDTO.getDomainStatus()).build()).domainGroup(permissionsDTO.getDomainGroup()).noDomainReason(permissionsDTO.getNoDomainReason()).smartitStatus(Dict.builder().id(permissionsDTO.getSmartitStatus()).build()).noSmartitReason(permissionsDTO.getNoSmartitReason()).usbStatus(Dict.builder().id(permissionsDTO.getUsbStatus()).build()).usbReason(permissionsDTO.getUsbReason()).usbExpireDate(permissionsDTO.getUsbExpireDate()).antivirusStatus(Dict.builder().id(permissionsDTO.getAntivirusStatus()).build()).noSymantecReason(permissionsDTO.getNoSymantecReason()).remark(permissionsDTO.getRemark()).createTime(Instant.now())
//                        .creater(jwtTokenProvider.getUserIdFromToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJKUzIxMTUiLCJzdWIiOiJKUzIxMTUiLCJpYXQiOjE3Njc1OTE4NzgsImV4cCI6MTc2NzY3ODI3OH0.FV_jjUTSWvYEeTYgFtb2iPkalIz48NK_2lTgi-HtWVk"))
                .creater("JS2115").updateTime(Instant.now())
//                        .updater(jwtTokenProvider.getUserIdFromToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJKUzIxMTUiLCJzdWIiOiJKUzIxMTUiLCJpYXQiOjE3Njc1OTE4NzgsImV4cCI6MTc2NzY3ODI3OH0.FV_jjUTSWvYEeTYgFtb2iPkalIz48NK_2lTgi-HtWVk"))
                .updater("JS2115").build());

        return permissionsDTO;
    }

    public PermissionsDTO updatePermissions(PermissionsDTO permissionsDTO) {
        DevicePermission devicePermission = devicePermissionRepository.findDevicePermissionByPermissionId(permissionsDTO.getPermissionId());
        if (devicePermission == null) {
            throw new BusinessException(30012, "未找到该权限信息，无法更新");
        }

        if (permissionsDTO.getDomainStatus() != null) {
            devicePermission.setDomainStatus(Dict.builder().id(permissionsDTO.getDomainStatus()).build());
        }
        if (permissionsDTO.getDomainGroup() != null) {
            devicePermission.setDomainGroup(permissionsDTO.getDomainGroup());
        }
        if (permissionsDTO.getNoDomainReason() != null) {
            devicePermission.setNoDomainReason(permissionsDTO.getNoDomainReason());
        }
        if (permissionsDTO.getSmartitStatus() != null) {
            devicePermission.setSmartitStatus(Dict.builder().id(permissionsDTO.getSmartitStatus()).build());
        }
        if (permissionsDTO.getNoSmartitReason() != null) {
            devicePermission.setNoSmartitReason(permissionsDTO.getNoSmartitReason());
        }
        if (permissionsDTO.getUsbStatus() != null) {
            devicePermission.setUsbStatus(Dict.builder().id(permissionsDTO.getUsbStatus()).build());
        }
        if (permissionsDTO.getUsbReason() != null) {
            devicePermission.setUsbReason(permissionsDTO.getUsbReason());
        }
        if (permissionsDTO.getUsbExpireDate() != null) {
            devicePermission.setUsbExpireDate(permissionsDTO.getUsbExpireDate());
        }
        if (permissionsDTO.getAntivirusStatus() != null) {
            devicePermission.setAntivirusStatus(Dict.builder().id(permissionsDTO.getAntivirusStatus()).build());
        }
        if (permissionsDTO.getNoSymantecReason() != null) {
            devicePermission.setNoSymantecReason(permissionsDTO.getNoSymantecReason());
        }
        if (permissionsDTO.getRemark() != null) {
            devicePermission.setRemark(permissionsDTO.getRemark());
        }

        devicePermission.setUpdater("JS2115");
        devicePermission.setUpdateTime(Instant.now());

        try {
            devicePermissionRepository.save(devicePermission);
        } catch (Exception e) {
            throw new BusinessException(30006, e.getMessage());
        }
        return permissionsDTO;
    }

    public String deletePermissions(String id) {
        DevicePermission devicePermission = devicePermissionRepository.findDevicePermissionByPermissionId(id);
        if (devicePermission == null) {
            throw new BusinessException(30007, "没有找到该权限信息");
        }
        devicePermissionRepository.delete(devicePermission);
        return id;
    }
}



/*

public class DevicePermission {
    @Id
    @Size(max = 50)
    @Column(name = "permission_id", nullable = false, length = 50)
    private String permissionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceInfo device;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "domain_status_id")
    private Dict domainStatus;

    @Size(max = 100)
    @Column(name = "domain_group", length = 100)
    private String domainGroup;

    @Column(name = "no_domain_reason", length = Integer.MAX_VALUE)
    private String noDomainReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "smartit_status_id")
    private Dict smartitStatus;

    @Column(name = "no_smartit_reason", length = Integer.MAX_VALUE)
    private String noSmartitReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "usb_status_id")
    private Dict usbStatus;

    @Column(name = "usb_reason", length = Integer.MAX_VALUE)
    private String usbReason;

    @Column(name = "usb_expire_date")
    private LocalDate usbExpireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "antivirus_status_id")
    private Dict antivirusStatus;

    @Column(name = "no_symantec_reason", length = Integer.MAX_VALUE)
    private String noSymantecReason;

    @Column(name = "remark", length = Integer.MAX_VALUE)
    private String remark;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @Size(max = 100)
    @Column(name = "creater", length = 100)
    private String creater;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @Size(max = 100)
    @Column(name = "updater", length = 100)
    private String updater;
}
*/

//public class Dict {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "dict_id", nullable = false)
//    private Long id;
//
//    @Size(max = 50)
//    @NotNull
//    @Column(name = "dict_type_code", nullable = false, length = 50)
//    private String dictTypeCode;
//
//    @Size(max = 100)
//    @NotNull
//    @Column(name = "dict_type_name", nullable = false, length = 100)
//    private String dictTypeName;
//
//    @Column(name = "dict_type_description", length = Integer.MAX_VALUE)
//    private String dictTypeDescription;
//
//    @Size(max = 100)
//    @NotNull
//    @Column(name = "dict_item_name", nullable = false, length = 100)
//    private String dictItemName;
//
//    @ColumnDefault("0")
//    @Column(name = "sort")
//    private Integer sort;
//
//    @NotNull
//    @ColumnDefault("1")
//    @Column(name = "is_enabled", nullable = false)
//    private Short isEnabled;
//
//    @NotNull
//    @ColumnDefault("CURRENT_TIMESTAMP")
//    @Column(name = "create_time", nullable = false)
//    private Instant createTime;
//
//    @Size(max = 100)
//    @Column(name = "creater", length = 100)
//    private String creater;
//
//    @NotNull
//    @ColumnDefault("CURRENT_TIMESTAMP")
//    @Column(name = "update_time", nullable = false)
//    private Instant updateTime;
//
//    @Size(max = 100)
//    @Column(name = "updater", length = 100)
//    private String updater;
//}