package com.device.management.repository;

import com.device.management.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * デバイスリポジトリ
 *
 * @author device-management
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {

    List<Device> findByDeviceId(String deviceId);

    /**
     * 条件付きクエリ（ページング対応）
     * 注意：LEFT JOIN FETCHを使用すると直接ページングできないため、ここではFETCHを使用しない
     * 関連データ（辞書、ユーザー等）は後続のクエリで取得
     *
     * @param userId     ユーザーID（完全一致）
     * @param pageable   ページングパラメータ
     * @return ページングされたデバイスリスト
     */
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN d.user u " +
            "WHERE (:userId IS NULL OR :userId = '' OR d.userId = :userId) " +
            "ORDER BY d.deviceId ASC")
    Page<Device> findByConditionsWithPagination(
            @Param("userId") String userId,
            Pageable pageable
    );

    /**
     * 条件付きクエリの総レコード数
     *
     * @param deviceName デバイス名（あいまい検索）
     * @param userId     ユーザーID（完全一致）
     * @return 総レコード数
     */
    @Query("SELECT COUNT(d) FROM Device d " +
            "LEFT JOIN d.user u " +
            "WHERE (:deviceName IS NULL OR :deviceName = '' OR d.computerName LIKE CONCAT('%', :deviceName, '%')) " +
            "AND (:userId IS NULL OR :userId = '' OR d.userId = :userId)")
    Long countByConditions(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId
    );

    /**
     * 条件付きクエリ（ページングなし、エクスポートなどのシナリオ用）
     * FETCH JOINを使用して関連データを事前に読み込み
     *
     * @param deviceName デバイス名（あいまい検索）
     * @param userId     ユーザーID（完全一致）
     * @return デバイスリスト
     */
    @Query("SELECT DISTINCT d FROM Device d " +
            "LEFT JOIN FETCH d.user u " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE (:deviceName IS NULL OR :deviceName = '' OR d.computerName LIKE CONCAT('%', :deviceName, '%')) " +
            "AND (:userId IS NULL OR :userId = '' OR d.userId = :userId) " +
            "ORDER BY d.deviceId ASC")
    List<Device> findByConditions(
            @Param("deviceName") String deviceName,
            @Param("userId") String userId
    );

    /**
     * デバイス詳細（辞書情報含む）
     *
     * @param deviceId デバイスID
     * @return デバイスエンティティ
     */
    @Query("SELECT d FROM Device d " +
            "LEFT JOIN FETCH d.user u " +
            "LEFT JOIN FETCH d.osDict " +
            "LEFT JOIN FETCH d.memoryDict " +
            "LEFT JOIN FETCH d.ssdDict " +
            "LEFT JOIN FETCH d.hddDict " +
            "LEFT JOIN FETCH d.selfConfirmDict " +
            "WHERE TRIM(d.deviceId) = :deviceId")
    Device findByDeviceIdWithDicts(@Param("deviceId") String deviceId);

    /**
     * デバイスIP情報の一括取得
     *
     * @param deviceIds デバイスIDリスト
     * @return デバイスIDとIPのマッピング関係
     */
    @Query("SELECT TRIM(d.device.deviceId) as deviceId, d FROM DeviceIp d WHERE d.device.deviceId IN :deviceIds")
    List<Object[]> findDeviceIpsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    /**
     * デバイスモニター情報の一括取得
     *
     * @param deviceIds デバイスIDリスト
     * @return デバイスIDとモニターのマッピング関係
     */
    @Query("SELECT TRIM(m.device.deviceId) as deviceId, m FROM Monitor m WHERE m.device.deviceId IN :deviceIds")
    List<Object[]> findMonitorsByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    /**
     * デバイスの存在確認
     *
     * @param deviceId デバイスID
     * @return 存在するかどうか
     */
    boolean existsByDeviceId(String deviceId);
}
