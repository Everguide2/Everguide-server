package com.example.everguide.web.dto.welfare;

import com.example.everguide.domain.WelfareService;
import jakarta.validation.constraints.NotNull;

public record WelfareServiceDetailResponse(
    @NotNull
    String servId,
    @NotNull
    String servNm,
    String servDgst,
    String servDtlLink,
    String intrsThemaNmArray,
    String bizChrDeptNm,
    String sggNm,
    String aplyMtdNm,
    String srvPvsnNm,
    String lastModYmd,
    boolean isBookmarked
) {
    public static WelfareServiceDetailResponse of(WelfareService entity, boolean isBookmarked) {
        return new WelfareServiceDetailResponse(
            entity.getServiceId(),
            entity.getServiceName(),
            entity.getServiceDigest(),
            entity.getServiceDetailLink(),
            entity.getSupportTypes(),
            entity.getChargeDepartment(),
            entity.getRegionDetail(),
            entity.getApplyMethod(),
            entity.getProvisionType(),
            entity.getLastModYmd(),
            isBookmarked
        );
    }
} 