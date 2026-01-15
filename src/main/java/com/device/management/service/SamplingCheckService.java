package com.device.management.service;

import com.device.management.dto.SamplingCheckDTO;
import com.device.management.entity.SamplingCheck;
import com.device.management.mapper.SamplingCheckMapper;
import com.device.management.repository.SamplingCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SamplingCheckService {
    @Autowired
    private SamplingCheckRepository samplingCheckRepository;
    @Autowired
    private SamplingCheckMapper samplingCheckMapper;

    //更新方法
    public SamplingCheckDTO update(String samplingId, SamplingCheckDTO dto) {
        if(!samplingCheckRepository.existsById(samplingId))  throw new RuntimeException("記録が存在しないため、更新できません");
        SamplingCheck entity = samplingCheckRepository.findById(samplingId).get();
        samplingCheckMapper.updateEntityFromDto(dto,entity);
        entity.setUpdateTime(LocalDateTime.now());
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }

    //追加方法
    public SamplingCheckDTO create(SamplingCheckDTO dto) {
        SamplingCheck entity = samplingCheckMapper.convertToEntity(dto);
        entity.setSamplingId(UUID.randomUUID().toString().replace("-",""));
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }

    //IDで検索
    public SamplingCheckDTO findById(String id) {
        log.info("find sampling check by id {}", id);
        SamplingCheck samplingCheck = samplingCheckRepository.findById(id).orElse(null);
        return samplingCheckMapper.convertToDto(samplingCheck);
    }

    //報告番号で検索
    public List<SamplingCheckDTO> findByReportId(String reportId) {
        log.info("find sampling check by reportId {}", reportId);
        List<SamplingCheck> samplingChecks = samplingCheckRepository.findByReportId(reportId);
        return samplingCheckMapper.convertToDto(samplingChecks);
    }

    //記録を削除する（記録が存在しない場合はエラー）
    public void delete(String samplingId) {
        log.info("delete sampling check {}", samplingId);
        if (!samplingCheckRepository.existsById(samplingId)) {
            throw new RuntimeException("Not found");
        }
        samplingCheckRepository.deleteById(samplingId);
    }


    //すべてのレコードを問い合せます（改ページしない）
    public List<SamplingCheckDTO> findAll() {
        log.info("find all sampling checks");
        List<SamplingCheck> samplingChecks =  samplingCheckRepository.findAll();
        return  samplingCheckMapper.convertToDto(samplingChecks);
    }

    //すべてのレコードの問合せ（ページング）
    public Page<SamplingCheckDTO> findAll(Pageable pageable) {
        log.info("find all sampling checks {}", pageable);
        Page<SamplingCheck>  samplingChecks =  samplingCheckRepository.findAll(pageable);
        return samplingCheckMapper.convertToDto(samplingChecks);
    }

    //オプション条件によるレコードのフィルタ
    public Page<SamplingCheckDTO> getSamplingChecks(int page, int size, String deviceId, String userId) {
        log.info("get sampling checks {} {} {} {}", page, size, deviceId, userId);   //ログを記録する
        Pageable pageable = PageRequest.of(page - 1, size);                //ページング要件の準備
        Page<SamplingCheck> samplingChecks;
        if (deviceId != null && !deviceId.isEmpty() && userId != null && !userId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByDeviceIdAndUserId(deviceId,userId,pageable);  //デバイスIDとユーザIDで調べる
        }else if (deviceId != null && !deviceId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByDeviceId(deviceId,pageable);                  //デバイスID別に調べる
        }else if (userId != null && !userId.isEmpty()) {
            samplingChecks = samplingCheckRepository.findByUserId(userId,pageable);                      //ユーザーID別に調べる
        }else  {
            samplingChecks = samplingCheckRepository.findAll(pageable);                                  //制約なし
        }

        return samplingCheckMapper.convertToDto(samplingChecks);                                         //SamplingCheckMapperを使用してentityをdtoに変換
    }

    public void deleteByDeviceId(String deviceId) {
        log.info("delete by device id {}", deviceId);
        samplingCheckRepository.deleteByDeviceId(deviceId);
    }

}
