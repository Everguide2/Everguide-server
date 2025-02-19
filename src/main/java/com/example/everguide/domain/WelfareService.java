package com.example.everguide.domain;

import jakarta.persistence.*;
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
    @Column(name = "serv_id", nullable = false)
    private String servId;      // 정책 API의 서비스 ID (기본키)

    @Column(name = "serv_nm", length = 500)
    private String servNm;  // 서비스명

    @Column(name = "serv_dgst", length = 1000)
    private String servDgst;    // 서비스 요약

    @Column(name = "serv_dtl_link", length = 500)
    private String servDtlLink; // 서비스상세링크

    @Column(name = "aply_mtd_nm", length = 500)
    private String aplyMtdNm;   //신청방법명

    @Column(name = "biz_chr_dept_nm", length = 200)
    private String bizChrDeptNm;    // 사업담당부서명

    @Column(name = "ctpv_nm", length = 100)
    private String ctpvNm;  // 시도명

    @Column(name = "inq_num")
    private int inqNum; // 조회수

    @Column(name = "intrs_thema_nm_array", length = 500)
    private String intrsThemaNmArray;   // 관심주제명

    @Column(name = "last_mod_ymd", length = 8)
    private String lastModYmd;  // 최종수정일자

    @Column(name = "life_nm_array", length = 500)
    private String lifeNmArray; // 생애주기명

    @Column(name = "sprt_cyc_nm", length = 100)
    private String sprtCycNm;   // 지원주기명

    @Column(name = "srv_pvsn_nm", length = 200)
    private String srvPvsnNm;   // 제공유형명

    @Column(name = "trgter_indvdl_nm_array", length = 500)
    private String trgterIndvdlNmArray; // 가구상황명

    @Column(name = "sgg_nm", length = 100)
    private String sggNm;   // 시군구명
}
