package com.example.everguide.web.dto.welfare;

import java.util.List;

public record WelfareServiceSearchRequest(
    String keyword,
    List<String> intrsThemaNmArray,     // 분야 (다중 선택 가능)
    String ctpvNm,                      // 시도명 (서울, 경기 등)
    String lastModYmd                   // 마감일
) {
    public WelfareServiceSearchRequest {
        keyword = (keyword == null) ? "" : keyword;
        intrsThemaNmArray = (intrsThemaNmArray == null) ? List.of() : intrsThemaNmArray;
        ctpvNm = (ctpvNm == null) ? "" : ctpvNm;
        lastModYmd = (lastModYmd == null) ? "" : lastModYmd;
    }
} 