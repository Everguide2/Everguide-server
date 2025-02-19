package com.example.everguide.web.dto.job;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class JobItemDetail {

    @XmlElement(name = "acptMthdCd")
    private String acptMthdCd; // 접수 방법

    @XmlElement(name = "age")
    private String age; // 연령

    @XmlElement(name = "ageLim")
    private String ageLim; // 연령 제한

    @XmlElement(name = "clerk")
    private String clerk; // 담당자

    @XmlElement(name = "clerkContt")
    private String clerkContt; // 담당자 연락처

    @XmlElement(name = "clltPrnnum")
    private String clltPrnnum; // 채용공고 모집인원

    @XmlElement(name = "createDy")
    private String createDy; // 생성일자

    @XmlElement(name = "detCnts")
    private String detCnts; // 상세내용

    @XmlElement(name = "etcItm")
    private String etcItm; // 기타사항

    @XmlElement(name = "frAcptDd")
    private String frAcptDd; // 시작접수일

    @XmlElement(name = "homepage")
    private String homepage; // 홈페이지

    @XmlElement(name = "jobId")
    private String jobId; // 채용공고ID

    @XmlElement(name = "lnkStmId")
    private String lnkStmId; // 연계시스템 ID

    @XmlElement(name = "organYn")
    private String organYn; // 대민: N, 업무: Y

    @XmlElement(name = "plDetAddr")
    private String plDetAddr; // 주소

    @XmlElement(name = "plbizNm")
    private String plbizNm; // 사업장명

    @XmlElement(name = "repr")
    private String repr; // 담당자

    @XmlElement(name = "stmId")
    private String stmId; // 시스템ID

    @XmlElement(name = "toAcptDd")
    private String toAcptDd; // 종료접수일

    @XmlElement(name = "updDy")
    private String updDy; // 변경일자

    @XmlElement(name = "wantedAuthNo")
    private String wantedAuthNo; // 구인인증번호

    @XmlElement(name = "wantedTitle")
    private String wantedTitle; // 채용제목
}
