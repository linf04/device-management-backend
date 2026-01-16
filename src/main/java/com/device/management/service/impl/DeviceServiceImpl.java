package com.device.management.service.impl;

import com.device.management.dto.ApiResponse;
import com.device.management.dto.DeviceFullDTO;
import com.device.management.dto.DictDTO;
import com.device.management.entity.Device;
import com.device.management.entity.DeviceIp;
import com.device.management.entity.Monitor;
import com.device.management.entity.User;
import com.device.management.exception.InvalidIpAddressException;
import com.device.management.exception.ParameterException;
import com.device.management.exception.ResourceConflictException;
import com.device.management.exception.ResourceNotFoundException;
import com.device.management.repository.DeviceIpRepository;
import com.device.management.repository.DeviceRepository;
import com.device.management.repository.MonitorRepository;
import com.device.management.repository.SamplingCheckRepository;
import com.device.management.repository.UserRepository;
import com.device.management.service.DeviceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * デバイス管理サービス実装クラス
 * デバイスのCRUD操作、リスト照会機能を提供
 *
 * @author device-management
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private MonitorRepository monitorRepository;
    
    @Autowired
    private DeviceIpRepository deviceIpRepository;
    
    @Autowired
    private SamplingCheckRepository samplingCheckRepository;
    
    @Autowired
    private UserRepository userRepository;

    private static final InetAddressValidator IP_VALIDATOR = InetAddressValidator.getInstance();
    private static final int MAX_PAGE_SIZE = 100;

    // ==================== 公共業務メソッド ====================

    @Override
    @Transactional
    public ApiResponse<DeviceFullDTO> insertDevice(DeviceFullDTO dto) {
        log.info("デバイス追加: {}", dto.getDeviceId());

        // 基本パラメータ検証
        validateBasicParams(dto.getDeviceId(), dto.getCreater());

        // 業務競合チェック
        validateDeviceNotExists(dto.getDeviceId());
        validateUserId(dto.getUserId());
        validateMonitors(dto.getMonitorsInput(), null);
        validateDeviceIps(dto.getDeviceIpsInput(), null);

        // デバイス及び関連データ保存
        Device entity = convertToDeviceEntity(dto);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        Device saved = deviceRepository.save(entity);

        List<Monitor> monitors = saveMonitors(dto.getMonitorsInput(), saved.getDeviceId(), dto.getCreater(), dto.getUpdater());
        List<DeviceIp> ips = saveDeviceIps(dto.getDeviceIpsInput(), saved.getDeviceId(), dto.getCreater(), dto.getUpdater());

        DeviceFullDTO result = buildDeviceFullDTO(saved, monitors, ips, dto);
        return ApiResponse.success("デバイス追加成功", result);
    }

    @Override
    @Transactional
    public ApiResponse<DeviceFullDTO> updateDeviceById(String deviceId, DeviceFullDTO dto) {
        log.info("デバイス更新: {}", deviceId);

        // パラメータ検証
        if (!StringUtils.hasText(deviceId)) {
            throw new ParameterException("デバイスIDは空にできません");
        }
        if (dto == null) {
            throw new ParameterException("リクエストボディは空にできません");
        }

        // デバイス存在チェック
        Device existDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("デバイスが存在しません: " + deviceId));

        // 業務競合チェック
        validateUserId(dto.getUserId());

        // 古いモニターとIPを先に削除し、その後新しいものを検証（既存データの競合回避）
        deleteMonitorsByDeviceId(deviceId);
        deleteDeviceIpsByDeviceId(deviceId);

        // 新しいモニターとIPを検証（古いデータが削除されたため、他のデバイスによる占有を正しく検証可能）
        validateMonitors(dto.getMonitorsInput(), null);
        validateDeviceIps(dto.getDeviceIpsInput(), null);

        // デバイス及び関連データ更新
        Device entity = convertToDeviceEntity(dto);
        entity.setCreateTime(existDevice.getCreateTime());
        entity.setUpdateTime(LocalDateTime.now());
        Device saved = deviceRepository.save(entity);

        List<Monitor> monitors = saveMonitors(dto.getMonitorsInput(), saved.getDeviceId(), dto.getUpdater(), dto.getUpdater());
        List<DeviceIp> ips = saveDeviceIps(dto.getDeviceIpsInput(), saved.getDeviceId(), dto.getUpdater(), dto.getUpdater());

        DeviceFullDTO result = buildDeviceFullDTO(saved, monitors, ips, dto);
        return ApiResponse.success("デバイス更新成功", result);
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteDevice(String deviceId) {
        log.info("デバイス削除: {}", deviceId);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("デバイスが存在しません: " + deviceId));

        // 関連データをカスケード削除
        samplingCheckRepository.deleteByDeviceId(deviceId);
        deleteMonitorsByDeviceId(deviceId);
        deleteDeviceIpsByDeviceId(deviceId);
        deviceRepository.delete(device);

        return ApiResponse.success("デバイス削除成功", deviceId);
    }

    @Override
    public Page<DeviceFullDTO> list(String userId, int page, int size) {
        log.info("機器一覧をクエリ: userId={}, page={}, size={}", userId, page, size);

        // パラメータ検証
        validatePageParams(page, size);

        // userIdが提供された場合、ユーザーが存在するかを検証
        if (StringUtils.hasText(userId)) {
            validateUserId(userId);
        }

        // ページングパラメータを構築
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("deviceId").ascending());

        // JPAページングクエリを使用
        Page<Device> devicePage = deviceRepository.findByConditionsWithPagination(
                StringUtils.hasText(userId) ? userId : null,
                pageable
        );

        // 現在ページの機器一覧を取得
        List<Device> devices = devicePage.getContent();

        // 関連データを一括取得（IPとモニター）
        List<String> deviceIds = devices.stream()
                .map(d -> d.getDeviceId().trim())
                .collect(Collectors.toList());
        Map<String, List<DeviceIp>> ipMap = buildDeviceIpMap(deviceIds);
        Map<String, List<Monitor>> monitorMap = buildDeviceMonitorMap(deviceIds);

        // DTOに変換
        List<DeviceFullDTO> dtoList = devices.stream()
                .map(d -> toFullDTOWithRelations(d, ipMap, monitorMap))
                .collect(Collectors.toList());

        // ページング結果を返却
        return new PageImpl<>(dtoList, pageable, devicePage.getTotalElements());
    }

    @Override
    public ApiResponse<DeviceFullDTO> detail(String deviceId) {
        log.info("デバイス詳細照会: {}", deviceId);

        if (!StringUtils.hasText(deviceId)) {
            throw new ParameterException("デバイスIDは空にできません");
        }

        Device device = deviceRepository.findByDeviceIdWithDicts(deviceId.trim());
        if (device == null) {
            throw new ResourceNotFoundException("デバイスが存在しません: " + deviceId.trim());
        }

        List<String> ids = Collections.singletonList(device.getDeviceId().trim());
        DeviceFullDTO dto = toFullDTOWithRelations(device,
                buildDeviceIpMap(ids),
                buildDeviceMonitorMap(ids));
        return ApiResponse.success("照会成功", dto);
    }

    // ==================== プライベート検証メソッド ====================

    /**
     * 基本パラメータ検証
     */
    private void validateBasicParams(String deviceId, String creater) {
        if (!StringUtils.hasText(deviceId)) {
            throw new ParameterException("デバイスIDは空にできません");
        }
        if (!StringUtils.hasText(creater)) {
            throw new ParameterException("作成者は空にできません");
        }
    }

    /**
     * ページングパラメータ検証
     */
    private void validatePageParams(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("ページ番号は1以上でなければなりません");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("1ページのサイズは1から" + MAX_PAGE_SIZE + "の間でなければなりません");
        }
    }

    /**
     * デバイスが存在しないことを検証
     */
    private void validateDeviceNotExists(String deviceId) {
        if (deviceRepository.existsByDeviceId(deviceId)) {
            throw new ResourceConflictException("デバイスが既に存在します: " + deviceId);
        }
    }

    /**
     * ユーザーIDが存在することを検証（user_idが提供された場合）
     */
    private void validateUserId(String userId) {
        if (StringUtils.hasText(userId)) {
            if (!userRepository.findByUserId(userId).isPresent()) {
                throw new ResourceNotFoundException("ユーザーIDが存在しません: " + userId);
            }
        }
    }

    /**
     * モニター検証（統一メソッド、追加・更新シナリオ対応）
     *
     * @param monitors モニターリスト
     * @param deviceId デバイスID（更新シナリオで渡す、追加シナリオではnull）
     */
    private void validateMonitors(List<Monitor> monitors, String deviceId) {
        if (CollectionUtils.isEmpty(monitors)) {
            return;
        }

        Set<String> names = new HashSet<>();
        for (Monitor monitor : monitors) {
            String monitorName = monitor.getMonitorName();
            if (!StringUtils.hasText(monitorName)) {
                throw new ParameterException("モニター名は空にできません");
            }

            // 检查重复
            if (names.contains(monitorName)) {
                throw new ParameterException("モニター名が重複しています: " + monitorName);
            }
            names.add(monitorName);

            // 检查是否被其他设备占用
            if (monitorRepository.existsByMonitorName(monitorName)) {
                Monitor exist = monitorRepository.findByMonitorName(monitorName);
                // 更新场景：如果是当前设备的监视器，允许；否则报错
                if (deviceId == null || !exist.getDeviceId().equals(deviceId)) {
                    throw new ResourceConflictException("モニター名が他のデバイスで使用されています: " + monitorName + ", 设备ID: " + exist.getDeviceId());
                }
            }
        }
    }

    /**
     * IPアドレス検証（統一メソッド、追加・更新シナリオ対応）
     *
     * @param deviceIps IPアドレスリスト
     * @param deviceId  デバイスID（更新シナリオで渡す、追加シナリオではnull）
     */
    private void validateDeviceIps(List<DeviceIp> deviceIps, String deviceId) {
        if (CollectionUtils.isEmpty(deviceIps)) {
            return;
        }

        Set<String> ipSet = new HashSet<>();
        for (DeviceIp ip : deviceIps) {
            String addr = ip.getIpAddress() != null ? ip.getIpAddress().trim() : null;
            if (!StringUtils.hasText(addr)) {
                throw new ParameterException("IPアドレスは空にできません");
            }

            // 重複チェック
            if (ipSet.contains(addr)) {
                throw new ParameterException("IPアドレスが重複しています: " + addr);
            }
            ipSet.add(addr);

            // 形式検証
            validateIpv4(addr);

            // 他のデバイスで占有されているかチェック
            if (deviceIpRepository.existsByIpAddress(addr)) {
                DeviceIp exist = deviceIpRepository.findByIpAddress(addr);
                // 更新シナリオ：現在のデバイスのIPであれば許可；それ以外はエラー
                if (deviceId == null || !exist.getDeviceId().equals(deviceId)) {
                    throw new ResourceConflictException("IPアドレスが他のデバイスで使用されています: " + addr + ", デバイスID: " + exist.getDeviceId());
                }
            }
        }
    }

    /**
     * IPv4アドレス形式検証
     */
    private void validateIpv4(String addr) {
        if (!IP_VALIDATOR.isValidInet4Address(addr)) {
            log.warn("IPアドレス形式が無効: {}", addr);
            throw new InvalidIpAddressException("IPアドレス形式が無効: " + addr);
        }
    }

    // ==================== プライベート変換メソッド ====================

    /**
     * DTOをエンティティに変換
     */
    private Device convertToDeviceEntity(DeviceFullDTO dto) {
        Device device = new Device();
        device.setDeviceId(dto.getDeviceId());
        device.setDeviceModel(dto.getDeviceModel());
        device.setComputerName(dto.getComputerName());
        device.setLoginUsername(dto.getLoginUsername());
        device.setProject(dto.getProject());
        device.setDevRoom(dto.getDevRoom());
        device.setUserId(dto.getUserId());
        device.setRemark(dto.getRemark());
        device.setSelfConfirmId(dto.getSelfConfirmId());
        device.setOsId(dto.getOsId());
        device.setMemoryId(dto.getMemoryId());
        device.setSsdId(dto.getSsdId());
        device.setHddId(dto.getHddId());
        device.setCreater(dto.getCreater());
        device.setUpdater(dto.getUpdater());
        return device;
    }

    /**
     * 完全なデバイスDTO構築
     */
    private DeviceFullDTO buildDeviceFullDTO(Device device, List<Monitor> monitors, List<DeviceIp> ips, DeviceFullDTO orig) {
        DeviceFullDTO dto = new DeviceFullDTO();
        dto.setDeviceId(device.getDeviceId());
        dto.setDeviceModel(device.getDeviceModel());
        dto.setComputerName(device.getComputerName());
        dto.setLoginUsername(device.getLoginUsername());
        dto.setProject(device.getProject());
        dto.setDevRoom(device.getDevRoom());
        dto.setUserId(device.getUserId());
        dto.setRemark(device.getRemark());

        dto.setSelfConfirmId(device.getSelfConfirmId());
        dto.setOsId(device.getOsId());
        dto.setMemoryId(device.getMemoryId());
        dto.setSsdId(device.getSsdId());
        dto.setHddId(device.getHddId());
        dto.setCreater(device.getCreater());
        dto.setUpdater(device.getUpdater());
        dto.setMonitorsInput(monitors != null ? monitors : Collections.emptyList());
        dto.setDeviceIpsInput(ips != null ? ips : Collections.emptyList());
        dto.setName(orig != null ? orig.getName() : null);
        dto.setDeptId(orig != null ? orig.getDeptId() : null);
        return dto;
    }

    /**
     * 関連データを含む完全なDTOに変換
     */
    private DeviceFullDTO toFullDTOWithRelations(Device device,
                                                  Map<String, List<DeviceIp>> ipMap,
                                                  Map<String, List<Monitor>> monitorMap) {
        DeviceFullDTO dto = toFullBasicDTO(device);
        String key = device.getDeviceId().trim();
        List<DeviceIp> ips = ipMap.getOrDefault(key, Collections.emptyList());
        List<Monitor> monitors = monitorMap.getOrDefault(key, Collections.emptyList());
        dto.setDeviceIpsInput(ips);
        dto.setMonitorsInput(monitors);
        return dto;
    }

    /**
     * 基本DTOに変換（関連データを含まない）
     */
    private DeviceFullDTO toFullBasicDTO(Device device) {
        User user = device.getUser();
        DeviceFullDTO dto = DeviceFullDTO.builder()
                .deviceId(device.getDeviceId().trim())
                .userId(device.getUserId())
                .name(user != null ? user.getName() : null)
                .deptId(user != null ? user.getDeptId() : null)
                .deviceModel(device.getDeviceModel())
                .computerName(device.getComputerName())
                .loginUsername(device.getLoginUsername())
                .project(device.getProject())
                .devRoom(device.getDevRoom())
                .remark(device.getRemark())
                .selfConfirmId(device.getSelfConfirmId())
                .osId(device.getOsId())
                .memoryId(device.getMemoryId())
                .ssdId(device.getSsdId())
                .hddId(device.getHddId())
                .createTime(device.getCreateTime())
                .creater(device.getCreater())
                .updateTime(device.getUpdateTime())
                .updater(device.getUpdater())
                .monitorsInput(Collections.emptyList())
                .deviceIpsInput(Collections.emptyList())
                .build();
        
        // 辞書データ設定
        dto.setSelfConfirmDict(DictDTO.fromEntity(device.getSelfConfirmDict()));
        dto.setOsDict(DictDTO.fromEntity(device.getOsDict()));
        dto.setMemoryDict(DictDTO.fromEntity(device.getMemoryDict()));
        dto.setSsdDict(DictDTO.fromEntity(device.getSsdDict()));
        dto.setHddDict(DictDTO.fromEntity(device.getHddDict()));
        
        return dto;
    }

    // ==================== プライベートデータ操作メソッド ====================

    /**
     * モニターリスト保存
     */
    private List<Monitor> saveMonitors(List<Monitor> list, String deviceId, String creater, String updater) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        return list.stream()
                .map(m -> {
                    // 新しいMonitorオブジェクトを作成し、主キーが正しく生成されることを確認
                    Monitor monitor = new Monitor();
                    monitor.setMonitorId(null); // 明示的にnullを設定し、主キー自動生成を確実にする
                    monitor.setMonitorName(m.getMonitorName());
                    monitor.setDeviceId(deviceId);
                    monitor.setCreater(creater);
                    monitor.setUpdater(updater);
                    monitor.setCreateTime(now);
                    monitor.setUpdateTime(now);
                    return monitorRepository.saveAndFlush(monitor);
                })
                .collect(Collectors.toList());
    }

    /**
     * IPアドレスリスト保存
     */
    private List<DeviceIp> saveDeviceIps(List<DeviceIp> list, String deviceId, String creater, String updater) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        return list.stream()
                .map(ip -> {
                    // 新しいDeviceIpオブジェクトを作成し、主キーが正しく生成されることを確認
                    DeviceIp deviceIp = new DeviceIp();
                    deviceIp.setIpId(null); // 明示的にnullを設定し、主キー自動生成を確実にする
                    deviceIp.setIpAddress(ip.getIpAddress());
                    deviceIp.setDeviceId(deviceId);
                    deviceIp.setCreater(creater);
                    deviceIp.setUpdater(updater);
                    deviceIp.setCreateTime(now);
                    deviceIp.setUpdateTime(now);
                    return deviceIpRepository.saveAndFlush(deviceIp);
                })
                .collect(Collectors.toList());
    }

    /**
     * デバイスのすべてのモニターを削除
     */
    private void deleteMonitorsByDeviceId(String deviceId) {
        List<Monitor> monitors = monitorRepository.findByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(monitors)) {
            monitorRepository.deleteAll(monitors);
        }
    }

    /**
     * デバイスのすべてのIPアドレスを削除
     */
    private void deleteDeviceIpsByDeviceId(String deviceId) {
        List<DeviceIp> deviceIps = deviceIpRepository.findByDeviceId(deviceId);
        if (!CollectionUtils.isEmpty(deviceIps)) {
            deviceIpRepository.deleteAll(deviceIps);
        }
    }

    /**
     * デバイスIPアドレスマッピング構築（統一メソッド）
     */
    private Map<String, List<DeviceIp>> buildDeviceIpMap(List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) {
            return Collections.emptyMap();
        }
        return buildEntityMap(deviceRepository.findDeviceIpsByDeviceIds(deviceIds), DeviceIp.class);
    }

    /**
     * デバイスモニターマッピング構築（統一メソッド）
     */
    private Map<String, List<Monitor>> buildDeviceMonitorMap(List<String> deviceIds) {
        if (CollectionUtils.isEmpty(deviceIds)) {
            return Collections.emptyMap();
        }
        return buildEntityMap(deviceRepository.findMonitorsByDeviceIds(deviceIds), Monitor.class);
    }

    /**
     * エンティティマッピングの汎用メソッド構築
     */
    @SuppressWarnings("unchecked")
    private <T> Map<String, List<T>> buildEntityMap(List<Object[]> rows, Class<T> entityClass) {
        Map<String, List<T>> map = new HashMap<>();
        for (Object[] row : rows) {
            if (row.length >= 2 && row[0] != null && row[1] != null) {
                String deviceId = ((String) row[0]).trim();
                T entity = (T) row[1];
                map.computeIfAbsent(deviceId, k -> new ArrayList<>()).add(entity);
            }
        }
        return map;
    }
}
