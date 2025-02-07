package com.example.everguide.web.dto.job;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class JobItem {

    @XmlElement(name = "dstrCd1Nm")
    private String regionSido;  // 시도

    @XmlElement(name = "dstrCd2Nm")
    private String regionSigungu; // 시군구

    @XmlElement(name = "hpInvtCnt")
    private int recruitCnt; // 구직인원

    @XmlElement(name = "hpNotiEdate")
    private String postingEndDate; // 공고 종료일

    @XmlElement(name = "hpNotiSdate")
    private String postingStartDate; // 공고 시작일

    @XmlElement(name = "intCnt")
    private int wage; // 임금

    @XmlElement(name = "orgName")
    private String companyName; // 기관명

    @XmlElement(name = "projNo")
    private String jobCode; // 프로젝트 번호

    @XmlElement(name = "projYear")
    private int projectYear; // 사업년도

    @XmlElement(name = "trnStatNm")
    @Enumerated(EnumType.STRING)
    private String hireType; // 구인상태

    @XmlElement(name = "jobType")
    @Enumerated(EnumType.STRING)
    private String jobType; // 유형

    @XmlElement(name = "workPlace")
    private String workPlace; //workPlace


}