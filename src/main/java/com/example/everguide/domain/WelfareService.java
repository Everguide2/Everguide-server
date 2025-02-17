package com.example.everguide.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "welfare_service")
@Getter
@Setter
@NoArgsConstructor
public class WelfareService {

    @Id
    private String servId;      // 정책 API의 서비스 ID (기본키)

    private String servNm; // 서비스명
    private String servDgst; // 서비스 요약
    private String servDtlLink; // 서비스 상세 링크
    private String aplyMtdNm; // 신청방법명
    private String bizChrDeptNm; // 사업담당부서명
    private String ctpvNm; // 시도명
    private int inqNum; // 조회수
    private String intrsThemaNmArray; // 관심주제명
    private String lastModYmd; // 최종수정일자
    private String lifeNmArray; // 생애주기명
    private String sprtCycNm; // 지원주기명
    private String srvPvsnNm; // 제공유형명
    private String trgterIndvdlNmArray; // 가구상황명
    private String sggNm; // 시군구명
}
