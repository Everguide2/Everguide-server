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
                    .servId(welfare.getServId())
                    .servNm(welfare.getServNm())
                    .servDgst(welfare.getServDgst())
                    .ctpvNm(welfare.getCtpvNm())
                    .sggNm(welfare.getSggNm())
                    .intrsThemaNmArray(welfare.getIntrsThemaNmArray())
                    .bizChrDeptNm(welfare.getBizChrDeptNm())
                    .srvPvsnNm(welfare.getSrvPvsnNm())
                    .aplyMtdNm(welfare.getAplyMtdNm())
                    .inqNum(welfare.getInqNum())
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
                    .servId(welfare.getServId())
                    .servNm(welfare.getServNm())
                    .servDgst(welfare.getServDgst())
                    .servDtlLink(welfare.getServDtlLink())
                    .aplyMtdNm(welfare.getAplyMtdNm())
                    .bizChrDeptNm(welfare.getBizChrDeptNm())
                    .ctpvNm(welfare.getCtpvNm())
                    .sggNm(welfare.getSggNm())
                    .inqNum(welfare.getInqNum())
                    .intrsThemaNmArray(welfare.getIntrsThemaNmArray())
                    .lifeNmArray(welfare.getLifeNmArray())
                    .sprtCycNm(welfare.getSprtCycNm())
                    .srvPvsnNm(welfare.getSrvPvsnNm())
                    .trgterIndvdlNmArray(welfare.getTrgterIndvdlNmArray())
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
