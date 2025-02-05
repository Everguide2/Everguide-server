package com.example.everguide.service.welfare;


import com.example.everguide.domain.WelfareService;
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
