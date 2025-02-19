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
            entity.getServId(),
            entity.getServNm(),
            entity.getIntrsThemaNmArray(),
            entity.getBizChrDeptNm(),
            entity.getSggNm(),
            entity.getAplyMtdNm(),
            entity.getSrvPvsnNm(),
            entity.getLastModYmd()
        );
    }
} 