package com.device.management.service;

import com.device.management.dto.SamplingCheckDTO;
import com.device.management.entity.SamplingCheck;
import com.device.management.mapper.SamplingCheckMapper;
import com.device.management.repository.SamplingCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SamplingCheckService {
    @Autowired
    private SamplingCheckRepository samplingCheckRepository;
    @Autowired
    private SamplingCheckMapper samplingCheckMapper;

    //更新方法
    public SamplingCheckDTO update(String samplingId, SamplingCheckDTO dto) {
        if(!samplingCheckRepository.existsById(samplingId))  throw new RuntimeException("记录不存在，无法更新");
        SamplingCheck entity = samplingCheckRepository.findById(samplingId).get();
        samplingCheckMapper.updateEntityFromDto(dto,entity);
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }



    //删除记录(记录不存在报错）
    public void delete(String samplingId) {
        log.info("delete sampling check {}", samplingId);
        if (!samplingCheckRepository.existsById(samplingId)) {
            throw new RuntimeException("Not found");
        }
        samplingCheckRepository.deleteById(samplingId);
    }

    //删除记录(记录不存在不报错）
    public void deleteById(String id) {
        log.info("delete sampling check by id {}", id);
        samplingCheckRepository.deleteById(id);
    }


