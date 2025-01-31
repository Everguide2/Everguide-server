package com.example.everguide.web.dto.policy;

import lombok.Builder;

@Builder
public record PolicyApiRequest(
        String serviceKey,
        String pageNo,
        String numOfRows,
        String lifeArray,
        String trgterIndvdlArray,
        String intrsThemaArray,
        String age,
        String ctpvNm,
        String sggNm,
        String srchKeyCode,
        String searchWrd,
        String arrgOrd
) {
    public static PolicyApiRequest of(
            String serviceKey,
            String pageNo,
            String numOfRows,
            String lifeArray,
            String trgterIndvdlArray,
            String intrsThemaArray,
            String age,
            String ctpvNm,
            String sggNm,
            String srchKeyCode,
            String searchWrd,
            String arrgOrd
    ) {
        return PolicyApiRequest.builder()
                .serviceKey(serviceKey)
                .pageNo(pageNo)
                .numOfRows(numOfRows)
                .lifeArray(lifeArray)
                .trgterIndvdlArray(trgterIndvdlArray)
                .intrsThemaArray(intrsThemaArray)
                .age(age)
                .ctpvNm(ctpvNm)
                .sggNm(sggNm)
                .srchKeyCode(srchKeyCode)
                .searchWrd(searchWrd)
                .arrgOrd(arrgOrd)
                .build();
    }
} 