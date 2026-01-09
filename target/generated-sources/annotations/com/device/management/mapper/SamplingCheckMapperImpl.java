package com.device.management.mapper;

import com.device.management.dto.SamplingCheckDTO;
import com.device.management.entity.SamplingCheck;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-08T16:39:34+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SamplingCheckMapperImpl implements SamplingCheckMapper {

    @Override
    public SamplingCheckDTO convertToDto(SamplingCheck samplingCheck) {
        if ( samplingCheck == null ) {
            return null;
        }

        SamplingCheckDTO samplingCheckDTO = new SamplingCheckDTO();

        samplingCheckDTO.setSamplingId( samplingCheck.getSamplingId() );
        samplingCheckDTO.setReportId( samplingCheck.getReportId() );
        samplingCheckDTO.setUserId( samplingCheck.getUserId() );
        samplingCheckDTO.setName( samplingCheck.getName() );
        samplingCheckDTO.setDeviceId( samplingCheck.getDeviceId() );
        samplingCheckDTO.setUpdateDate( samplingCheck.getUpdateDate() );
        samplingCheckDTO.setUpdateTime( samplingCheck.getUpdateTime() );
        samplingCheckDTO.setCreateTime( samplingCheck.getCreateTime() );
        samplingCheckDTO.setUpdater( samplingCheck.getUpdater() );
        samplingCheckDTO.setCreater( samplingCheck.getCreater() );
        samplingCheckDTO.setInstalledSoftware( samplingCheck.getInstalledSoftware() );
        samplingCheckDTO.setDisposalMeasures( samplingCheck.getDisposalMeasures() );
        samplingCheckDTO.setScreenSaverPwd( samplingCheck.getScreenSaverPwd() );
        samplingCheckDTO.setUsbInterface( samplingCheck.getUsbInterface() );
        samplingCheckDTO.setSecurityPatch( samplingCheck.getSecurityPatch() );
        samplingCheckDTO.setAntivirusProtection( samplingCheck.getAntivirusProtection() );
        samplingCheckDTO.setBootAuthentication( samplingCheck.getBootAuthentication() );

        return samplingCheckDTO;
    }

    @Override
    public List<SamplingCheckDTO> convertToDto(List<SamplingCheck> samplingChecks) {
        if ( samplingChecks == null ) {
            return null;
        }

        List<SamplingCheckDTO> list = new ArrayList<SamplingCheckDTO>( samplingChecks.size() );
        for ( SamplingCheck samplingCheck : samplingChecks ) {
            list.add( convertToDto( samplingCheck ) );
        }

        return list;
    }

    @Override
    public SamplingCheck convertToEntity(SamplingCheckDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SamplingCheck samplingCheck = new SamplingCheck();

        samplingCheck.setSamplingId( dto.getSamplingId() );
        samplingCheck.setName( dto.getName() );
        samplingCheck.setUserId( dto.getUserId() );
        samplingCheck.setReportId( dto.getReportId() );
        samplingCheck.setDeviceId( dto.getDeviceId() );
        samplingCheck.setUpdateDate( dto.getUpdateDate() );
        samplingCheck.setInstalledSoftware( dto.getInstalledSoftware() );
        samplingCheck.setDisposalMeasures( dto.getDisposalMeasures() );
        samplingCheck.setScreenSaverPwd( dto.getScreenSaverPwd() );
        samplingCheck.setUsbInterface( dto.getUsbInterface() );
        samplingCheck.setSecurityPatch( dto.getSecurityPatch() );
        samplingCheck.setAntivirusProtection( dto.getAntivirusProtection() );
        samplingCheck.setBootAuthentication( dto.getBootAuthentication() );
        samplingCheck.setCreateTime( dto.getCreateTime() );
        samplingCheck.setCreater( dto.getCreater() );
        samplingCheck.setUpdateTime( dto.getUpdateTime() );
        samplingCheck.setUpdater( dto.getUpdater() );

        return samplingCheck;
    }

    @Override
    public void updateEntityFromDto(SamplingCheckDTO dto, SamplingCheck entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setUserId( dto.getUserId() );
        entity.setReportId( dto.getReportId() );
        entity.setDeviceId( dto.getDeviceId() );
        entity.setUpdateDate( dto.getUpdateDate() );
        entity.setInstalledSoftware( dto.getInstalledSoftware() );
        entity.setDisposalMeasures( dto.getDisposalMeasures() );
        entity.setScreenSaverPwd( dto.getScreenSaverPwd() );
        entity.setUsbInterface( dto.getUsbInterface() );
        entity.setSecurityPatch( dto.getSecurityPatch() );
        entity.setAntivirusProtection( dto.getAntivirusProtection() );
        entity.setBootAuthentication( dto.getBootAuthentication() );
        entity.setCreateTime( dto.getCreateTime() );
        entity.setCreater( dto.getCreater() );
        entity.setUpdateTime( dto.getUpdateTime() );
        entity.setUpdater( dto.getUpdater() );
    }
}
