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
    private int totalCount;

    @JacksonXmlProperty(localName = "pageNo")
    private int pageNo;

    @JacksonXmlProperty(localName = "numOfRows")
    private int numOfRows;

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
        @JacksonXmlProperty(localName = "aplyMtdNm")
        private String applyMethod;

        @JacksonXmlProperty(localName = "bizChrDeptNm")
        private String chargeDepartment;

        @JacksonXmlProperty(localName = "ctpvNm")
        private String targetRegion;

        @JacksonXmlProperty(localName = "inqNum")
        private Integer inquiryNumber;

        @JacksonXmlProperty(localName = "intrsThemaNmArray")
        private List<String> supportTypes;

        @JacksonXmlProperty(localName = "lastModYmd")
        @JsonFormat(pattern = "yyyyMMdd")
        private LocalDateTime lastModifiedDate;

        @JacksonXmlProperty(localName = "lifeNmArray")
        private List<String> lifeArray;

        @JacksonXmlProperty(localName = "servDgst")
        private String serviceDigest;

        @JacksonXmlProperty(localName = "servDtlLink")
        private String serviceDetailLink;

        @JacksonXmlProperty(localName = "servId")
        private String serviceId;

        @JacksonXmlProperty(localName = "servNm")
        private String serviceName;

        @JacksonXmlProperty(localName = "sprtCycNm")
        private String supportCycle;

        @JacksonXmlProperty(localName = "srvPvsnNm")
        private String provisionType;

        @JacksonXmlProperty(localName = "trgterIndvdlNmArray")
        private List<String> householdConditions;
    }

    public List<PolicyItem> getPolicyItems() {
        return this.servList;
    }
} 