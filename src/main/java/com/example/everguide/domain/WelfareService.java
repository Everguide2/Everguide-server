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
    private String servNm;

    @Column(name = "serv_dgst", length = 1000)
    private String servDgst;

    @Column(name = "serv_dtl_link", length = 500)
    private String servDtlLink;

    @Column(name = "aply_mtd_nm", length = 500)
    private String aplyMtdNm;

    @Column(name = "biz_chr_dept_nm", length = 200)
    private String bizChrDeptNm;

    @Column(name = "ctpv_nm", length = 100)
    private String ctpvNm;

    @Column(name = "inq_num")
    private int inqNum;

    @Column(name = "intrs_thema_nm_array", length = 500)
    private String intrsThemaNmArray;

    @Column(name = "last_mod_ymd", length = 8)
    private String lastModYmd;

    @Column(name = "life_nm_array", length = 500)
    private String lifeNmArray;

    @Column(name = "sprt_cyc_nm", length = 100)
    private String sprtCycNm;

    @Column(name = "srv_pvsn_nm", length = 200)
    private String srvPvsnNm;

    @Column(name = "trgter_indvdl_nm_array", length = 500)
    private String trgterIndvdlNmArray;

    @Column(name = "sgg_nm", length = 100)
    private String sggNm;
}
