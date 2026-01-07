package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.PermissionsDTO;
import com.device.management.entity.DeviceInfo;
import com.device.management.entity.DevicePermission;
import com.device.management.entity.Dict;
import com.device.management.entity.User;
import com.device.management.exception.BusinessException;
import com.device.management.repository.DevicePermissionRepository;
import com.device.management.repository.DeviceRepository;
import com.device.management.repository.DictRepository;
import com.device.management.repository.UserRepository;
import com.device.management.security.JwtTokenProvider;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DevicePermissionService {
    @Resource
    DevicePermissionRepository devicePermissionRepository;
    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private DictRepository dictRepository;
    @Resource
    DeviceRepository deviceRepository;
    @Resource
    private UserRepository userRepository;

    public ApiResponse<List<PermissionsDTO>> getPermissions(Integer page, Integer size, User user, DeviceInfo deviceInfo) {
        // 验证分页参数
        if (page == null || page < 0) {
            page = 1;
        }
        if (size == null || size <= 0) {
            size = 10;
        } else if (size > 20) {
            size = 20; // 限制每页最大数量
        }

        // 构建分页对象，按权限编号排列
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "permissionId"));

        // 构建查询条件
        Specification<DevicePermission> spec = buildQuerySpecification(user, deviceInfo);

        // 执行分页查询
        Page<DevicePermission> permissionPage = devicePermissionRepository.findAll(spec, pageable);

        // 转换为DTO列表
        List<PermissionsDTO> dtoList = convertToDTOList(permissionPage.getContent());

        // 构建响应
        return ApiResponse.page(
                dtoList,
                permissionPage.getTotalElements(),
                permissionPage.getNumber() + 1,
                permissionPage.getSize()
        );
    }

    //构建查询条件 - 只根据用户ID和设备ID
    private Specification<DevicePermission> buildQuerySpecification(User user, DeviceInfo deviceInfo) {
        return (Root<DevicePermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 根据用户的userid查询
            if (user != null && StringUtils.hasText(user.getUserId())) {
                // 通过DeviceInfo关联的User查询
                Join<DevicePermission, DeviceInfo> deviceJoin = root.join("device", JoinType.INNER);
                Join<DeviceInfo, User> userJoin = deviceJoin.join("user", JoinType.INNER);
                predicates.add(cb.equal(userJoin.get("userId"), user.getUserId()));

                // 验证用户是否存在
                User existingUser = userRepository.findByUserId(user.getUserId());
                if (existingUser == null) {
                    throw new BusinessException(30003, "用户不存在");
                }
            }

            // 根据设备ID查询
            if (deviceInfo != null && StringUtils.hasText(deviceInfo.getDeviceId())) {
                Join<DevicePermission, DeviceInfo> deviceJoin = root.join("device", JoinType.INNER);
                predicates.add(cb.equal(deviceJoin.get("deviceId"), deviceInfo.getDeviceId()));

                // 验证设备是否存在
                DeviceInfo existingDevice = deviceRepository.findByDeviceId(deviceInfo.getDeviceId());
                if (existingDevice == null) {
                    throw new BusinessException(30001, "设备不存在");
                }
            }

            // 如果没有任何条件，返回所有记录
            if (predicates.isEmpty()) {
                return cb.conjunction(); // 返回 true，即查询所有
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    //将实体列表转换为DTO列表
    private List<PermissionsDTO> convertToDTOList(List<DevicePermission> permissions) {
        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //将单个实体转换为DTO
    private PermissionsDTO convertToDTO(DevicePermission permission) {
        if (permission == null) {
            return null;
        }

        PermissionsDTO dto = new PermissionsDTO();
        dto.setPermissionId(permission.getPermissionId());

        // 设备和用户信息
        if (permission.getDevice() != null) {
            dto.setDeviceId(permission.getDevice().getDeviceId());
            dto.setComputerName(permission.getDevice().getComputerName());
            dto.setLoginUsername(permission.getDevice().getLoginUsername());
            dto.setUserId(permission.getDevice().getUser().getUserId());
            dto.setName(permission.getDevice().getUser().getName());
            dto.setDeptId(permission.getDevice().getUser().getDeptId());
            dto.setIpAddress(permission.getDevice().getDeviceIp().getIpAddress());

        }

        // 用户信息

        // 域状态
        if (permission.getDomainStatus() != null) {
            dto.setDomainStatus(permission.getDomainStatus().getSort() != null ?
                    permission.getDomainStatus().getSort() : 0);
        }
        dto.setDomainGroup(permission.getDomainGroup());
        dto.setNoDomainReason(permission.getNoDomainReason());

        // Smartit状态
        if (permission.getSmartitStatus() != null) {
            dto.setSmartitStatus(permission.getSmartitStatus().getSort() != null ?
                    permission.getSmartitStatus().getSort() : 0);
        }
        dto.setNoSmartitReason(permission.getNoSmartitReason());

        // USB状态
        if (permission.getUsbStatus() != null) {
            dto.setUsbStatus(permission.getUsbStatus().getSort() != null ?
                    permission.getUsbStatus().getSort() : 0);
        }
        dto.setUsbReason(permission.getUsbReason());
        dto.setUsbExpireDate(permission.getUsbExpireDate());

        // 防病毒状态
        if (permission.getAntivirusStatus() != null) {
            dto.setAntivirusStatus(permission.getAntivirusStatus().getSort() != null ?
                    permission.getAntivirusStatus().getSort() : 0);
        }
        dto.setNoSymantecReason(permission.getNoSymantecReason());
        dto.setRemark(permission.getRemark());
        dto.setCreateTime(permission.getCreateTime());
        dto.setCreater(permission.getCreater());
        dto.setUpdateTime(permission.getUpdateTime());
        dto.setUpdater(permission.getUpdater());

        return dto;
    }

    public ApiResponse<PermissionsDTO> addPermissions(PermissionsDTO permissionsDTO) {
        DeviceInfo deviceInfo =   deviceRepository.findByDeviceId(permissionsDTO.getDeviceId());
        if (deviceInfo == null) {
            throw new BusinessException(30001,"设备不存在");
        }

        DevicePermission devicePermissions = devicePermissionRepository.findDevicePermissionsByDevice(deviceInfo);

        if (devicePermissions != null) {
            throw new BusinessException(30002,"设备已存在权限信息");
        }

        devicePermissionRepository.save(DevicePermission.builder()
                .permissionId(UUID.randomUUID().toString())
                .device(deviceInfo)
                .domainStatus(
                        dictRepository.findByDictTypeCodeAndSort("DOMAIN_STATUS",permissionsDTO.getDomainStatus())
                )
                .domainGroup(permissionsDTO.getDomainGroup())
                .noDomainReason(permissionsDTO.getNoDomainReason())
                .smartitStatus(
                        dictRepository.findByDictTypeCodeAndSort("SMARTIT_STATUS",permissionsDTO.getDomainStatus())
                )
                .noSmartitReason(permissionsDTO.getNoSmartitReason())
                .usbStatus(
                        dictRepository.findByDictTypeCodeAndSort("USB_STATUS",permissionsDTO.getDomainStatus())
                )
                .usbReason(permissionsDTO.getUsbReason())
                .usbExpireDate(permissionsDTO.getUsbExpireDate())
                .antivirusStatus(
                        dictRepository.findByDictTypeCodeAndSort("ANTIVIRUS_STATUS",permissionsDTO.getDomainStatus())
                )
                .noSymantecReason(permissionsDTO.getNoSymantecReason())
                .remark(permissionsDTO.getRemark())
                .createTime(Instant.now())
//                        .creater(jwtTokenProvider.getUserIdFromToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJKUzIxMTUiLCJzdWIiOiJKUzIxMTUiLCJpYXQiOjE3Njc1OTE4NzgsImV4cCI6MTc2NzY3ODI3OH0.FV_jjUTSWvYEeTYgFtb2iPkalIz48NK_2lTgi-HtWVk"))
                .creater("JS2115")
                .updateTime(Instant.now())
//                        .updater(jwtTokenProvider.getUserIdFromToken("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJKUzIxMTUiLCJzdWIiOiJKUzIxMTUiLCJpYXQiOjE3Njc1OTE4NzgsImV4cCI6MTc2NzY3ODI3OH0.FV_jjUTSWvYEeTYgFtb2iPkalIz48NK_2lTgi-HtWVk"))
                .updater("JS2115")
                .build());

        return ApiResponse.success("添加成功",permissionsDTO);
    }

    public ApiResponse<PermissionsDTO> updatePermissions(PermissionsDTO permissionsDTO) {
        return null;
    }

    public ApiResponse<Void> deletePermissions(String id) {
        return null;
    }

    public ApiResponse<Void> exportPermissions(Integer page, Integer size, User user, DeviceInfo deviceInfo, String permissionInfo) {
        return null;
    }

}