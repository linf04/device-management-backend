package com.device.management.service;

import com.device.management.dto.SamplingCheckDTO;
import com.device.management.entity.SamplingCheck;
import com.device.management.mapper.SamplingCheckMapper;
import com.device.management.repository.SamplingCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
        if(!samplingCheckRepository.existsById(samplingId))  throw new RuntimeException("記録が存在しないため、更新できません");
        SamplingCheck entity = samplingCheckRepository.findById(samplingId).get();
        samplingCheckMapper.updateEntityFromDto(dto,entity);
        SamplingCheck saved = samplingCheckRepository.save(entity);

        return samplingCheckMapper.convertToDto(saved);
    }

    //追加方法
    public SamplingCheckDTO create(SamplingCheckDTO dto) {
        if(samplingCheckRepository.existsById(dto.getSamplingId()))  throw new RuntimeException("レコードは既に存在しているため、新規追加できません");
        SamplingCheck entity = samplingCheckMapper.convertToEntity(dto);
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

    //レコードを削除（レコードが存在しなくてもエラーは出ません）
    public void deleteById(String id) {
        log.info("delete sampling check by id {}", id);
        samplingCheckRepository.deleteById(id);
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
}

