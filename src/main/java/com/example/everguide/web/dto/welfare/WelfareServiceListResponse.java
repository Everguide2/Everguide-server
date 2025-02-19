package com.example.everguide.web.dto.welfare;

import com.example.everguide.domain.WelfareService;
import jakarta.validation.constraints.NotNull;

public record WelfareServiceListResponse(
    @NotNull
    String servId,
    @NotNull
    String servNm,
    String intrsThemaNmArray,
    String bizChrDeptNm,
    String sggNm,
    String aplyMtdNm,
    String srvPvsnNm,
    String lastModYmd
) {
    public static WelfareServiceListResponse from(WelfareService entity) {
        return new WelfareServiceListResponse(
            entity.getServiceId(),
            entity.getServiceName(),
            entity.getSupportTypes(),
            entity.getChargeDepartment(),
            entity.getRegionDetail(),
            entity.getApplyMethod(),
            entity.getProvisionType(),
            entity.getLastModYmd()
        );
    }
} 