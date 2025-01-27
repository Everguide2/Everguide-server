package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.survey.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Set;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private SurveyTarget target; // SELF, FAMILY, ELDERLY

    @Enumerated(EnumType.STRING)
    private Region region;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<SupportType> supportTypes; // 필요한 자원 (다중 선택)

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<HouseholdType> householdTypes; // 가구 상황 (다중 선택)

    @Enumerated(EnumType.STRING)
    private DisabilityGrade disabilityGrade; // 장애 등급

    private boolean livingAlone;

    @Enumerated(EnumType.STRING)
    private FamilyDistance familyDistance;

    @Enumerated(EnumType.STRING)
    private IncomeSupport incomeSupports; // 수입, 지원 여부

    @Enumerated(EnumType.STRING)
    private WelfareStatus welfareStatus; // 기초생활수급자, 차상위계층 상태

    @Enumerated(EnumType.STRING)
    private DeviceUsage deviceUsage;

}
