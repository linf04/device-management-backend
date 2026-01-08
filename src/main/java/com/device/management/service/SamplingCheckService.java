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

    //添加方法
    public SamplingCheckDTO create(SamplingCheckDTO dto) {
        if(samplingCheckRepository.existsById(dto.getSamplingId()))  throw new RuntimeException("记录已存在，无法新增");
        SamplingCheck entity = samplingCheckMapper.convertToEntity(dto);
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }

    //根据ID查询
    public SamplingCheckDTO findById(String id) {
        log.info("find sampling check by id {}", id);
        SamplingCheck samplingCheck = samplingCheckRepository.findById(id).orElse(null);
        return samplingCheckMapper.convertToDto(samplingCheck);
    }

    //根据报告编号查询
    public List<SamplingCheckDTO> findByReportId(String reportId) {
        log.info("find sampling check by reportId {}", reportId);
        List<SamplingCheck> samplingChecks = samplingCheckRepository.findByReportId(reportId);
        return samplingCheckMapper.convertToDto(samplingChecks);
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


    //查询所有记录（不分页）
    public List<SamplingCheckDTO> findAll() {
        log.info("find all sampling checks");
        List<SamplingCheck> samplingChecks =  samplingCheckRepository.findAll();
        return  samplingCheckMapper.convertToDto(samplingChecks);
    }

    //查询所有记录（分页）
    public Page<SamplingCheckDTO> findAll(Pageable pageable) {
        log.info("find all sampling checks {}", pageable);
        Page<SamplingCheck>  samplingChecks =  samplingCheckRepository.findAll(pageable);
        return samplingCheckMapper.convertToDto(samplingChecks);
    }

    //根据可选条件筛选记录
    public Page<SamplingCheckDTO> getSamplingChecks(int page, int size, String deviceId, String userId) {
        log.info("get sampling checks {} {} {} {}", page, size, deviceId, userId);   //记录日志
        Pageable pageable = PageRequest.of(page - 1, size);                //准备分页要求
        Page<SamplingCheck> samplingChecks;
        if (deviceId != null && !deviceId.isEmpty() && userId != null && !userId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByDeviceIdAndUserId(deviceId,userId,pageable);  //按设备id和用户id查
        }else if (deviceId != null && !deviceId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByDeviceId(deviceId,pageable);                  //按设备id查
        }else if (userId != null && !userId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByUserId(userId,pageable);                      //按用户id查
        }else  {
            samplingChecks = samplingCheckRepository.findAll(pageable);                                  //没有限制条件
        }

        return samplingCheckMapper.convertToDto(samplingChecks);                                         //用samplingCheckMapper把entity转换成dto
    }
}

