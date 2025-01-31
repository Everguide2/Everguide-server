package com.example.everguide.web.dto.policy;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@XmlRootElement(name = "OpenAPI_ServiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PolicyApiResponse {

    @XmlElement(name = "totalCount")
    // private int totalCount;
    private String totalCount;

    @XmlElement(name = "pageNo")
    // private int pageNo;
    private String pageNo;

    @XmlElement(name = "numOfRows")
    // private int numOfRows;
    private String numOfRows;

    @XmlElement(name = "resultCode")
    private String resultCode;

    @XmlElement(name = "resultMessage")
    private String resultMessage;

    @XmlElementWrapper(name = "servList")
    @XmlElement(name = "servList")
    private List<PolicyItem> servList;

    @Getter
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PolicyItem {
        @XmlElement(name = "bizChrDeptNm")
        private String bizChrDeptNm;  // 사업담당부서명

        @XmlElement(name = "ctpvNm")
        private String ctpvNm;        // 시도명

        @XmlElement(name = "sggNm")
        private String sggNm;         // 시군구명

        @XmlElement(name = "servDgst")
        private String servDgst;      // 서비스요약

        @XmlElement(name = "servDtlLink")
        private String servDtlLink;   // 서비스상세링크

        @XmlElement(name = "lifeNmArray")
        private String lifeNmArray;   // 생애주기명

        @XmlElement(name = "intrsThemaNmArray")
        private String intrsThemaNmArray;  // 관심주제명

        @XmlElement(name = "sprtCycNm")
        private String sprtCycNm;     // 지원주기명

        @XmlElement(name = "srvPvsnNm")
        private String srvPvsnNm;     // 제공유형명

        @XmlElement(name = "aplyMtdNm")
        private String aplyMtdNm;     // 신청방법명

        @XmlElement(name = "inqNum")
        private String inqNum;        // 조회수

        @XmlElement(name = "lastModYmd")
        private String lastModYmd;    // 최종수정일자

        @XmlElement(name = "servId")
        private String servId;        // 서비스ID

        @XmlElement(name = "servNm")
        private String servNm;        // 서비스명

        @XmlElement(name = "trgterIndvdlNmArray")
        private String trgterIndvdlNmArray;  // 가구상황명

    }
}