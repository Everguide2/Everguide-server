package com.example.everguide.web.dto.policy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JacksonXmlRootElement(localName = "wantedList")
public class PolicyApiResponse {

    @JacksonXmlProperty(localName = "totalCount")
    // private int totalCount;
    private String totalCount;

    @JacksonXmlProperty(localName = "pageNo")
    // private int pageNo;
    private String pageNo;

    @JacksonXmlProperty(localName = "numOfRows")
    // private int numOfRows;
    private String numOfRows;

    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;

    @JacksonXmlProperty(localName = "resultMessage")
    private String resultMessage;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "servList")
    private List<PolicyItem> servList;

    @Getter
    @NoArgsConstructor
    public static class PolicyItem {
        @JacksonXmlProperty(localName = "bizChrDeptNm")
        private String bizChrDeptNm;  // 사업담당부서명

        @JacksonXmlProperty(localName = "ctpvNm")
        private String ctpvNm;        // 시도명

        @JacksonXmlProperty(localName = "sggNm")
        private String sggNm;         // 시군구명

        @JacksonXmlProperty(localName = "servDgst")
        private String servDgst;      // 서비스요약

        @JacksonXmlProperty(localName = "servDtlLink")
        private String servDtlLink;   // 서비스상세링크

        @JacksonXmlProperty(localName = "lifeNmArray")
        private String lifeNmArray;   // 생애주기명

        @JacksonXmlProperty(localName = "intrsThemaNmArray")
        private String intrsThemaNmArray;  // 관심주제명

        @JacksonXmlProperty(localName = "sprtCycNm")
        private String sprtCycNm;     // 지원주기명

        @JacksonXmlProperty(localName = "srvPvsnNm")
        private String srvPvsnNm;     // 제공유형명

        @JacksonXmlProperty(localName = "aplyMtdNm")
        private String aplyMtdNm;     // 신청방법명

        @JacksonXmlProperty(localName = "inqNum")
        private String inqNum;        // 조회수

        @JacksonXmlProperty(localName = "lastModYmd")
        private String lastModYmd;    // 최종수정일자

        @JacksonXmlProperty(localName = "servId")
        private String servId;        // 서비스ID

        @JacksonXmlProperty(localName = "servNm")
        private String servNm;        // 서비스명

        @JacksonXmlProperty(localName = "trgterIndvdlNmArray")
        private String trgterIndvdlNmArray;  // 가구상황명

    }
}