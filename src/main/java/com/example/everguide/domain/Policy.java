package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.survey.HouseholdType;
import com.example.everguide.domain.enums.survey.LifeCycleType;
import com.example.everguide.domain.enums.survey.SupportType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Policy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region targetRegion; // ctpvNm 시도명

    @Column(nullable = false)
    private String targetRegionDetail; // sggNm 시군구명

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<SupportType> supportTypes; // intrsThemaNmArray 관심주제명

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<LifeCycleType> lifeCycleTypes;  // lifeArray 생애 주기

    @Column(nullable = false)
    private String applyMethod; // aplyMtdNm 신청방법명

    @Column(nullable = false)
    private String chargeDepartment; // bizChrDeptNm 사업담당부서명

    @Column(nullable = false)
    private String serviceDigest; // servDgst 서비스 요약

    @Column(nullable = false)
    private String serviceDetailLink; // servDtlLink 서비스상세링크

    @Column(nullable = false)
    private String serviceId; // servId 서비스ID

    @Column(nullable = false)
    private String serviceName; // servNm 서비스명

    @Column(nullable = false)
    private String supportCycle; // sprtCycNm 지원주기명

    @Column(nullable = false)
    private String provisionType; // srvPvsnNm 제공유형명

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<HouseholdType> householdConditions; // trgterIndvdlNmArray 가구상황명

    @Column(nullable = false)
    private Integer deadline;

    @Column(nullable = false)
    private LocalDateTime startDate; // enfcBgngYmd 시행시작일자

    @Column(nullable = false)
    private LocalDateTime endDate; // enfcEndYmd 시행종료일자

    @Column(nullable = false, length = 500)
    private String supportTargetContent; // sprtTrgtCn 지원대상 내용

    @Column(nullable = false, length = 500)
    private String selectCriteriaContent; // slctCritCn 선정기준 내용

    @Column(nullable = false, length = 500)
    private String allowanceServiceContent; // alwServCn 급여서비스 내용

    @Column(nullable = false, length = 500)
    private String applyMethodContent; // aplyMtdCn 신청방법 내용

    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    private List<PolicyRelatedInfo> policyRelatedInfos;
}
