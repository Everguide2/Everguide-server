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
            entity.getServId(),
            entity.getServNm(),
            entity.getServDgst(),
            entity.getServDtlLink(),
            entity.getIntrsThemaNmArray(),
            entity.getBizChrDeptNm(),
            entity.getSggNm(),
            entity.getAplyMtdNm(),
            entity.getSrvPvsnNm(),
            entity.getLastModYmd(),
            isBookmarked
        );
    }
} 