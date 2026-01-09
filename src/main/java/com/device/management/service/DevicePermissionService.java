package com.device.management.service;

import com.device.management.dto.PermissionInsertDTO;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import com.device.management.entity.Dict;
import com.device.management.exception.BusinessException;
import com.device.management.repository.DevicePermissionRepository;
import com.device.management.repository.DeviceRepository;
import com.device.management.repository.DictRepository;
import com.device.management.security.JwtTokenProvider;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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

    public PermissionInsertDTO addPermissions(PermissionInsertDTO permissionsDTO) {


        DeviceInfo deviceInfo = deviceRepository.findByDeviceId(permissionsDTO.getDeviceId());
        if (deviceInfo == null) {
            throw new BusinessException(30002, "デバイスが存在しません");
        }

        DevicePermission devicePermissions = devicePermissionRepository.findDevicePermissionsByDevice(deviceInfo);

        if (devicePermissions != null) {
            throw new BusinessException(30003, "デバイスにはすでに権限情報があります");
        }

        devicePermissionRepository.save(DevicePermission.builder().permissionId(UUID.randomUUID().toString()).device(deviceInfo).domainStatus(Dict.builder().id(permissionsDTO.getDomainStatus()).build()).domainGroup(permissionsDTO.getDomainGroup()).noDomainReason(permissionsDTO.getNoDomainReason()).smartitStatus(Dict.builder().id(permissionsDTO.getSmartitStatus()).build()).noSmartitReason(permissionsDTO.getNoSmartitReason()).usbStatus(Dict.builder().id(permissionsDTO.getUsbStatus()).build()).usbReason(permissionsDTO.getUsbReason()).usbExpireDate(permissionsDTO.getUsbExpireDate()).antivirusStatus(Dict.builder().id(permissionsDTO.getAntivirusStatus()).build()).noSymantecReason(permissionsDTO.getNoSymantecReason()).remark(permissionsDTO.getRemark()).createTime(Instant.now())
//                        .creater(jwtTokenProvider.getUserIdFromToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJKUzIxMTUiLCJzdWIiOiJKUzIxMTUiLCJpYXQiOjE3Njc1OTE4NzgsImV4cCI6MTc2NzY3ODI3OH0.FV_jjUTSWvYEeTYgFtb2iPkalIz48NK_2lTgi-HtWVk"))
                .creater("JS2115").updateTime(Instant.now())
//                        .updater(jwtTokenProvider.getUserIdFromToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJKUzIxMTUiLCJzdWIiOiJKUzIxMTUiLCJpYXQiOjE3Njc1OTE4NzgsImV4cCI6MTc2NzY3ODI3OH0.FV_jjUTSWvYEeTYgFtb2iPkalIz48NK_2lTgi-HtWVk"))
                .updater("JS2115").build());

        return permissionsDTO;
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