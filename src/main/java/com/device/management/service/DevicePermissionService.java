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
import jakarta.validation.constraints.Size;
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

        // 构建查询条件 - 使用 DISTINCT 避免重复数据
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

    // 构建查询条件
    private Specification<DevicePermission> buildQuerySpecification(User user, DeviceInfo deviceInfo) {
        return (Root<DevicePermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 使用 DISTINCT 避免一对多关系导致的数据重复
            if (query.getResultType().equals(DevicePermission.class)) {
                query.distinct(true);
            }

            // 根据用户的userid查询
            if (user != null && StringUtils.hasText(user.getUserId())) {
                // 通过DeviceInfo关联的User查询
                Join<DevicePermission, DeviceInfo> deviceJoin = root.join("device", JoinType.INNER);
                Join<DeviceInfo, User> userJoin = deviceJoin.join("user", JoinType.INNER);
                predicates.add(cb.equal(userJoin.get("userId"), user.getUserId()));
            }

            // 根据设备ID查询
            if (deviceInfo != null && StringUtils.hasText(deviceInfo.getDeviceId())) {
                Join<DevicePermission, DeviceInfo> deviceJoin = root.join("device", JoinType.INNER);
                predicates.add(cb.equal(deviceJoin.get("deviceId"), deviceInfo.getDeviceId()));
            }

            // 如果没有任何条件，返回所有记录
            if (predicates.isEmpty()) {
                return cb.conjunction(); // 返回 true，即查询所有
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // 将实体列表转换为DTO列表
    private List<PermissionsDTO> convertToDTOList(List<DevicePermission> permissions) {

        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }


    // 将单个实体转换为DTO - 处理多个IP地址和显示器ID
    private PermissionsDTO convertToDTO(DevicePermission permission) {
        if (permission == null) {
            return null;
        }

        PermissionsDTO dto = new PermissionsDTO();
        dto.setPermissionId(permission.getPermissionId());

        // 设备和用户信息
        if (permission.getDevice() != null) {
            DeviceInfo device = permission.getDevice();
            dto.setDeviceId(device.getDeviceId());
            dto.setComputerName(device.getComputerName());
            dto.setLoginUsername(device.getLoginUsername());
            dto.setUserId(device.getUser().getUserId());
            dto.setName(device.getUser().getName());
            dto.setDeptId(device.getUser().getDeptId());

            // 处理多个IP地址 - 从 deviceIps 列表中提取所有 IP 地址
            if (device.getDeviceIps() != null && !device.getDeviceIps().isEmpty()) {
                List<String> ipAddresses = device.getDeviceIps().stream()
                        .map(deviceIp -> deviceIp.getIpAddress())
                        .filter(ip -> ip != null && !ip.trim().isEmpty())
                        .collect(Collectors.toList());
                dto.setIpAddress(ipAddresses);
            } else {
                dto.setIpAddress(new ArrayList<>()); // 如果没有IP地址，返回空列表
            }

            // 处理显示器ID - 从 monitors 列表中提取所有显示器 ID
            if (device.getMonitors() != null && !device.getMonitors().isEmpty()) {
                List<@Size(max = 100) String> monitorNames = device.getMonitors().stream()
                        .map(monitorInfo -> monitorInfo.getMonitorName())
                        .filter(id -> id != null)
                        .collect(Collectors.toList());
                dto.setMonitorNames(monitorNames);
            } else {
                dto.setMonitorNames(new ArrayList<>()); // 如果没有显示器，返回空列表
            }
        } else {
            dto.setIpAddress(new ArrayList<>());
            dto.setMonitorNames(new ArrayList<>());
        }

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
}