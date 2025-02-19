package com.example.everguide.service.welfare;

import com.example.everguide.domain.WelfareService;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.web.dto.welfare.ServList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WelfareMappingService {

    // ServList DTO 리스트를 WelfareServiceEntity 리스트로 변환
    public List<WelfareService> convert(List<ServList> servLists) {
        return servLists.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    private WelfareService mapToEntity(ServList dto) {
        WelfareService entity = new WelfareService();
        entity.setServiceId(dto.getServId());
        entity.setServiceName(dto.getServNm());
        entity.setServiceDigest(dto.getServDgst());
        entity.setServiceDetailLink(dto.getServDtlLink());
        entity.setApplyMethod(dto.getAplyMtdNm());
        entity.setChargeDepartment(dto.getBizChrDeptNm());
        entity.setRegion(Region.fromString(dto.getCtpvNm()));
        entity.setRegionDetail(dto.getSggNm());
        entity.setHits(dto.getInqNum());
        entity.setSupportTypes(dto.getIntrsThemaNmArray());
        entity.setLastModYmd(dto.getLastModYmd());
        entity.setLifeCycle(dto.getLifeNmArray());
        entity.setSupportCycle(dto.getSprtCycNm());
        entity.setProvisionType(dto.getSrvPvsnNm());
        entity.setHouseholdConditions(dto.getTrgterIndvdlNmArray());
        return entity;
    }
}