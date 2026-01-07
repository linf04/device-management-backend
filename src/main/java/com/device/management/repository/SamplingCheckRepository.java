package com.device.management.repository;

import com.device.management.entity.SamplingCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface SamplingCheckRepository  extends JpaRepository<SamplingCheck, String> {
    List<SamplingCheck> findByReportId(String reportId);

    Page<SamplingCheck> findByDeviceIdAndUserId(String deviceId, String userId, Pageable pageable);

    Page<SamplingCheck> findByDeviceId(String deviceId, Pageable pageable);

    Page<SamplingCheck> findByUserId(String userId, Pageable pageable);
}
