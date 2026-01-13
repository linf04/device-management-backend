package com.device.management.service;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.PermissionInsertDTO;
import com.device.management.dto.PermissionsListDTO;
import com.device.management.entity.*;
import com.device.management.exception.BusinessException;
import com.device.management.exception.ConflictException;
import com.device.management.exception.ResourceNotFoundException;
import com.device.management.repository.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Resource
    private UserRepository userRepository;
    @Resource
    DeviceUsagePermissionRepository deviceUsagePermissionRepository;

    public PermissionInsertDTO addPermissions(PermissionInsertDTO permissionInsertDTO) {
        DeviceInfo deviceInfo = deviceRepository.findDeviceByDeviceId(permissionInsertDTO.getDeviceId());
        if (deviceInfo == null) {
            throw new BusinessException(30002, "デバイスが存在しません");
        }

        DevicePermission devicePermissions = devicePermissionRepository.findDevicePermissionsByDevice(deviceInfo);

        if (devicePermissions != null) {
            throw new BusinessException(30003, "デバイスにはすでに権限情報があります");
        }

        String permissionId = UUID.randomUUID().toString();

        devicePermissionRepository.save(DevicePermission.builder().permissionId(permissionId).device(deviceInfo).domainStatus(Dict.builder().id(permissionInsertDTO.getDomainStatus()).build()).domainGroup(permissionInsertDTO.getDomainGroup()).noDomainReason(permissionInsertDTO.getNoDomainReason()).smartitStatus(Dict.builder().id(permissionInsertDTO.getSmartitStatus()).build()).noSmartitReason(permissionInsertDTO.getNoSmartitReason()).usbStatus(Dict.builder().id(permissionInsertDTO.getUsbStatus()).build()).usbReason(permissionInsertDTO.getUsbReason()).usbExpireDate(permissionInsertDTO.getUsbExpireDate()).antivirusStatus(Dict.builder().id(permissionInsertDTO.getAntivirusStatus()).build()).noSymantecReason(permissionInsertDTO.getNoSymantecReason()).remark(permissionInsertDTO.getRemark()).createTime(LocalDateTime.now())
                .creater("JS2115").updateTime(LocalDateTime.now())
                .updater("JS2115").build());
        permissionInsertDTO.setPermissionId(permissionId);
        return permissionInsertDTO;
    }

    public ApiResponse<List<PermissionsListDTO>> getPermissions(Integer page, Integer size, User user, DeviceInfo deviceInfo) {
        // ページネーションパラメータを検証する
        if (page == null || page <= 0) {
            page = 1;
        }
        if (size == null || size <= 0) {
            size = 10;
        } else if (size > 20) {
            size = 20;
        }

        // ページ分割オブジェクトを構築し、権限番号で並べる
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "permissionId"));

        // クエリ条件を構築する
        Specification<DevicePermission> spec = buildQuerySpecification(user, deviceInfo);

        // ページネーションクエリを実行する
        Page<DevicePermission> permissionPage = devicePermissionRepository.findAll(spec, pageable);

        // DTOリストに変換する
        List<PermissionsListDTO> dtoList = convertToDTOList(permissionPage.getContent());

        // 応答を構築する
        return ApiResponse.page(
                dtoList,
                permissionPage.getTotalElements(),
                permissionPage.getNumber() + 1,
                permissionPage.getSize()
        );
    }

    // クエリ条件を構築する
    private Specification<DevicePermission> buildQuerySpecification(User user, DeviceInfo deviceInfo) {
        return (Root<DevicePermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // DISTINCT を使用して、1対多の関係によるデータの重複を回避する
            if (query.getResultType().equals(DevicePermission.class)) {
                query.distinct(true);
            }

            // ユーザーIDで検索
            if (user != null && StringUtils.hasText(user.getUserId())) {
                // DeviceInfoに関連付けられたユーザーを検索する
                Join<DevicePermission, DeviceInfo> deviceJoin = root.join("device", JoinType.INNER);
                Join<DeviceInfo, User> userJoin = deviceJoin.join("user", JoinType.INNER);
                predicates.add(cb.equal(userJoin.get("userId"), user.getUserId()));
            }

            // デバイスIDで検索
            if (deviceInfo != null && StringUtils.hasText(deviceInfo.getDeviceId())) {
                Join<DevicePermission, DeviceInfo> deviceJoin = root.join("device", JoinType.INNER);
                predicates.add(cb.equal(deviceJoin.get("deviceId"), deviceInfo.getDeviceId()));
            }

            // もし条件がなければ、すべての記録を返します
            if (predicates.isEmpty()) {
                return cb.conjunction(); // true を返し、すべてを検索します
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // エンティティのリストをDTOのリストに変換する
    private List<PermissionsListDTO> convertToDTOList(List<DevicePermission> permissions) {
        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 単一のエンティティをDTOに変換する
    private PermissionsListDTO convertToDTO(DevicePermission permission) {
        if (permission == null) {
            return null;
        }

        PermissionsListDTO dto = new PermissionsListDTO();
        dto.setPermissionId(permission.getPermissionId());

        // デバイスとユーザー情報
        if (permission.getDevice() != null) {
            DeviceInfo device = permission.getDevice();
            dto.setDeviceId(device.getDeviceId());
            dto.setComputerName(device.getComputerName());
            dto.setLoginUsername(device.getLoginUsername());
            dto.setUserId(device.getUser().getUserId());
            dto.setName(device.getUser().getName());
            dto.setDeptId(device.getUser().getDeptId());

            // 複数のIPアドレスを処理する - deviceIpsリストからすべてのIPアドレスを抽出する
            if (device.getDeviceIp() != null && !device.getDeviceIp().isEmpty()) {
                List<String> ipAddresses = device.getDeviceIp().stream()
                        .map(deviceIp -> deviceIp.getIpAddress())
                        .filter(ip -> ip != null && !ip.trim().isEmpty())
                        .collect(Collectors.toList());
                dto.setIpAddress(ipAddresses);
            } else {
                dto.setIpAddress(new ArrayList<>()); // IPアドレスがない場合は、空のリストを返します
            }

            // モニター ID の処理 - monitors リストからすべてのモニター ID を抽出
            if (device.getMonitor() != null && !device.getMonitor().isEmpty()) {
                List<@Size(max = 100) String> monitorNames = device.getMonitor().stream()
                        .map(monitorInfo -> monitorInfo.getMonitorName())
                        .filter(id -> id != null)
                        .collect(Collectors.toList());
                dto.setMonitorNames(monitorNames);
            } else {
                dto.setMonitorNames(new ArrayList<>()); // モニターがない場合は、空のリストを返します
            }
        }

        // 域状態
        if (permission.getDomainStatus() != null) {
            dto.setDomainStatus(permission.getDomainStatus().getSort() != null ?
                    permission.getDomainStatus().getSort() : 0);
        }
        dto.setDomainGroup(permission.getDomainGroup());
        dto.setNoDomainReason(permission.getNoDomainReason());

        // Smartitの状態
        if (permission.getSmartitStatus() != null) {
            dto.setSmartitStatus(permission.getSmartitStatus().getSort() != null ?
                    permission.getSmartitStatus().getSort() : 0);
        }
        dto.setNoSmartitReason(permission.getNoSmartitReason());

        // USBの状態
        if (permission.getUsbStatus() != null) {
            dto.setUsbStatus(permission.getUsbStatus().getSort() != null ?
                    permission.getUsbStatus().getSort() : 0);
        }
        dto.setUsbReason(permission.getUsbReason());
        dto.setUsbExpireDate(permission.getUsbExpireDate());

        // ウイルス対策の状態
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


    public void deletePermissionById(Long id) {

        System.out.println("デバイス使用権限の削除開始: id=" + id);

        // 1. APIのLong型IDをデータベースのString型permissionIdにマッピング
        String permissionId = String.valueOf(id);

        // 2. 権限の存在チェック
        DevicePermission permission = deviceUsagePermissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("権限が存在しません: " + id));

        // 4. TODO: 関連リソースのチェック（他のテーブルをクエリする必要あり）
        // 模擬：権限IDに"TEST"が含まれている場合、関連があるものとみなす
        if (permissionId.contains("TEST")) {
            throw new ConflictException("権限は既にリソースに紐づいているため、削除できません: " + id);
        }

        // 5. 物理削除の実行
        deviceUsagePermissionRepository.delete(permission);
    }
}
