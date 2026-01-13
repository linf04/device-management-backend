package com.device.management.repository;

import com.device.management.entity.Dict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictRepository extends JpaRepository<Dict, Long> {
    Optional<Dict> findByDictIdAndDictTypeCode(Long dictId, String dictTypeCode);

    List<Dict> findByDictTypeCode(String dictTypeCode);
}

