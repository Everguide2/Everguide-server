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



    @XmlElement(name = "deadline")
    private String deadline; // 마감 여부

    @XmlElement(name = "jobId")
    private String jobId; // 마감 여부

    @XmlElement(name = "frDd")
    private String frDd; // 시작 접수일


    @XmlElement(name = "oranNm")
    private String oranNm; // 기업명

    @XmlElement(name = "recrtTitle")
    private String recrtTitle; // 채용 제목

    @XmlElement(name = "toDd")
    private String toDd; // 종료 접수일

    @XmlElement(name = "workPlc")
    private String workPlc; // 근무지명


}