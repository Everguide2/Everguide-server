package com.example.everguide.web.dto.welfare;

import com.example.everguide.domain.WelfareService;

import lombok.Builder;

import java.util.List;

public class WelfareServiceResponse {

    @Builder
    public record WelfareServiceListResponse(
            List<WelfareInfo> welfareList,
            int totalPages,
            int totalElements,
            boolean hasNext
    ) {}

    @Builder
    public record WelfareInfo(
            String servId,
            String servNm,
            String servDgst,
            String ctpvNm,
            String sggNm,
            String intrsThemaNmArray,
            String bizChrDeptNm,
            String srvPvsnNm,
            String aplyMtdNm,
            int inqNum,
            String lastModYmd,
            boolean isBookmarked
    ) {
        public static WelfareInfo from(WelfareService welfare, boolean isBookmarked) {
            return WelfareInfo.builder()
                    .servId(welfare.getServiceId())
                    .servNm(welfare.getServiceName())
                    .servDgst(welfare.getServiceDigest())
                    .ctpvNm(welfare.getRegion().name())
                    .sggNm(welfare.getRegionDetail())
                    .intrsThemaNmArray(welfare.getSupportTypes())
                    .bizChrDeptNm(welfare.getChargeDepartment())
                    .srvPvsnNm(welfare.getProvisionType())
                    .aplyMtdNm(welfare.getApplyMethod())
                    .inqNum(welfare.getHits())
                    .lastModYmd(welfare.getLastModYmd())
                    .isBookmarked(isBookmarked)
                    .build();
        }
    }

    @Builder
    public record WelfareServiceDetailResponse(
            String servId,
            String servNm,
            String servDgst,
            String servDtlLink,
            String aplyMtdNm,
            String bizChrDeptNm,
            String ctpvNm,
            String sggNm,
            int inqNum,
            String intrsThemaNmArray,
            String lifeNmArray,
            String sprtCycNm,
            String srvPvsnNm,
            String trgterIndvdlNmArray,
            String lastModYmd,
            boolean isBookmarked
    ) {
        public static WelfareServiceDetailResponse from(WelfareService welfare, boolean isBookmarked) {
            return WelfareServiceDetailResponse.builder()
                    .servId(welfare.getServiceId())
                    .servNm(welfare.getServiceName())
                    .servDgst(welfare.getServiceDigest())
                    .servDtlLink(welfare.getServiceDetailLink())
                    .aplyMtdNm(welfare.getApplyMethod())
                    .bizChrDeptNm(welfare.getChargeDepartment())
                    .ctpvNm(welfare.getRegion().name())
                    .sggNm(welfare.getRegionDetail())
                    .inqNum(welfare.getHits())
                    .intrsThemaNmArray(welfare.getSupportTypes())
                    .lifeNmArray(welfare.getLifeCycle())
                    .sprtCycNm(welfare.getSupportCycle())
                    .srvPvsnNm(welfare.getProvisionType())
                    .trgterIndvdlNmArray(welfare.getHouseholdConditions())
                    .lastModYmd(welfare.getLastModYmd())
                    .isBookmarked(isBookmarked)
                    .build();
        }
    }

    @Builder
    public record WelfareServiceFilterResponse(
            List<String> lifeNmList,
            List<String> trgterIndvdlNmList,
            List<String> intrsThemaNmList,
            List<String> ctpvNmList,
            List<String> sggNmList
    ) {}
}
