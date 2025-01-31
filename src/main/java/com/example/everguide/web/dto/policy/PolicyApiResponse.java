package com.example.everguide.web.dto.policy;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@XmlRootElement(name = "wantedList")
@XmlAccessorType(XmlAccessType.FIELD)
public class PolicyApiResponse {
    
    @XmlElement(name = "resultCode")
    private String resultCode;
    
    @XmlElement(name = "resultMessage")
    private String resultMessage;
    
    @XmlElement(name = "numOfRows")
    private String numOfRows;
    
    @XmlElement(name = "pageNo")
    private String pageNo;
    
    @XmlElement(name = "totalCount")
    private String totalCount;
    
    @XmlElement(name = "servList")
    private List<PolicyInfo> servList;
    
    @Getter
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PolicyInfo {
        @XmlElement(name = "bizChrDeptNm")
        private String bizChrDeptNm;
        
        @XmlElement(name = "ctpvNm")
        private String ctpvNm;
        
        @XmlElement(name = "sggNm")
        private String sggNm;
        
        @XmlElement(name = "servDgst")
        private String servDgst;
        
        @XmlElement(name = "servDtlLink")
        private String servDtlLink;
        
        @XmlElement(name = "lifeNmArray")
        private String lifeNmArray;
        
        @XmlElement(name = "intrsThemaNmArray")
        private String intrsThemaNmArray;
        
        @XmlElement(name = "sprtCycNm")
        private String sprtCycNm;
        
        @XmlElement(name = "srvPvsnNm")
        private String srvPvsnNm;
        
        @XmlElement(name = "aplyMtdNm")
        private String aplyMtdNm;
        
        @XmlElement(name = "inqNum")
        private String inqNum;
        
        @XmlElement(name = "lastModYmd")
        private String lastModYmd;
        
        @XmlElement(name = "servId")
        private String servId;
        
        @XmlElement(name = "servNm")
        private String servNm;
        
        @XmlElement(name = "trgterIndvdlNmArray")
        private String trgterIndvdlNmArray;
    }
}