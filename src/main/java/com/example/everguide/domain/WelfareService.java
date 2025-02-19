package com.example.everguide.domain;

import com.example.everguide.domain.enums.Region;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private String serviceId;      // servId 정책 API의 서비스 ID (기본키)

    @Column(name = "service_name", length = 500)
    private String serviceName; // servNm 서비스명

    @Column(name = "service_Digest", length = 1000)
    private String serviceDigest; // servDgst 서비스 요약

    @Column(name = "service_datil_link", length = 500)
    private String serviceDetailLink; // servDtlLink 서비스 상세 링크

    @Column(name = "apply_Method", length = 500)
    private String applyMethod; // aplyMtdNm 신청방법명 다중

    @Column(name = "charge_dapartment", length = 200)
    private String chargeDepartment; // bizChrDeptNm 사업담당부서명

    @Column(name = "region", length = 100)
    private Region region; // ctpvNm 시도명

    @Column(name = "hits")
    private int hits; // inqNum 조회수

    @Column(name = "support_types", length = 500)
    private String supportTypes; // intrsThemaNmArray 관심주제명 다중

    @Column(name = "last_mod_ymd", length = 8)
    private String lastModYmd; // lastModYmd 최종수정일자

    @Column(name = "life_cycle", length = 500)
    private String lifeCycle; // lifeNmArray 생애주기명 다중

    @Column(name = "support_cycle", length = 100)
    private String supportCycle; // sprtCycNm 지원주기명

    @Column(name = "provision_type", length = 200)
    private String provisionType; // srvPvsnNm 제공유형명 다중

    @Column(name = "household_conditions", length = 500)
    private String householdConditions; // trgterIndvdlNmArray 가구상황명 다중

    @Column(name = "region_detail", length = 100)
    private String regionDetail; // sggNm 시군구명
}