package com.deviceManagement.repository;

import com.deviceManagement.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictRepository extends JpaRepository<Dict, Long> {
    Optional<Dict> findByDictIdAndDictTypeCode(Long dictId, String dictTypeCode);
}

