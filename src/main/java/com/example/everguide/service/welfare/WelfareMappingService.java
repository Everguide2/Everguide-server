package com.example.server.service;


import com.example.server.dto.welfare.ServList;
import com.example.server.entity.WelfareServiceEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WelfareMappingService {

    // ServList DTO 리스트를 WelfareServiceEntity 리스트로 변환
    public List<WelfareServiceEntity> convert(List<ServList> servLists) {
        return servLists.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    private WelfareServiceEntity mapToEntity(ServList dto) {
        WelfareServiceEntity entity = new WelfareServiceEntity();
        entity.setServId(dto.getServId());
        entity.setServNm(dto.getServNm());
        entity.setServDgst(dto.getServDgst());
        entity.setServDtlLink(dto.getServDtlLink());
        entity.setAplyMtdNm(dto.getAplyMtdNm());
        entity.setBizChrDeptNm(dto.getBizChrDeptNm());
        entity.setCtpvNm(dto.getCtpvNm());
        entity.setSggNm(dto.getSggNm());
        entity.setInqNum(dto.getInqNum());
        entity.setIntrsThemaNmArray(dto.getIntrsThemaNmArray());
        entity.setLastModYmd(dto.getLastModYmd());
        entity.setLifeNmArray(dto.getLifeNmArray());
        entity.setSprtCycNm(dto.getSprtCycNm());
        entity.setSrvPvsnNm(dto.getSrvPvsnNm());
        entity.setTrgterIndvdlNmArray(dto.getTrgterIndvdlNmArray());
        return entity;
    }
}
