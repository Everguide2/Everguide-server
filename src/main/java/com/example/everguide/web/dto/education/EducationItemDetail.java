package com.example.everguide.web.dto.education;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class EducationItemDetail {

    @XmlElement(name = "acptFrDd")
    private String acptFrDd; // 접수 시작일

    @XmlElement(name = "acptToDd")
    private String acptToDd; // 접수 종료일

    @XmlElement(name = "applyCnt")
    private String applyCnt; // 신청인원

    @XmlElement(name = "citiprovNo")
    private String citiprovNo; // 지역

    @XmlElement(name = "cnts")
    private String cnts; // 상세내용

    @XmlElement(name = "eduCrseNo")
    private String eduCrseNo; // 접수과정 번호

    @XmlElement(name = "eduTgt")
    private String eduTgt; // 대상

    @XmlElement(name = "lctreFrDd")
    private String lctreFrDd; // 강좌시작일

    @XmlElement(name = "lctreNm")
    private String lctreNm; // 강좌명

    @XmlElement(name = "lctreNo")
    private String lctreNo; // 강좌번호

    @XmlElement(name = "lctreSeq")
    private String lctreSeq; // 회차

    @XmlElement(name = "lctreTm")
    private String lctreTm; // 교육시간

    @XmlElement(name = "lctreToDd")
    private String lctreToDd; // 강좌종료일

    @XmlElement(name = "partcptAmt")
    private String partcptAmt; // 참가비

    @XmlElement(name = "reqMthd")
    private String reqMthd; // 신청방법

    @XmlElement(name = "stayngYn")
    private String stayngYn; // 숙박여부

    @XmlElement(name = "totAcptPrnnum")
    private String totAcptPrnnum; // 모집인원
}
