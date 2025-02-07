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

    private String servNm;
    private String servDgst;
    private String servDtlLink;
    private String aplyMtdNm;
    private String bizChrDeptNm;
    private String ctpvNm;
    private int inqNum;
    private String intrsThemaNmArray;
    private String lastModYmd;
    private String lifeNmArray;
    private String sprtCycNm;
    private String srvPvsnNm;
    private String trgterIndvdlNmArray;
    private String sggNm;
}
