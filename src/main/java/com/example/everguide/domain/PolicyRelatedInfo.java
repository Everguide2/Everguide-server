package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PolicyRelatedInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private String welfareInfoDetailCode; // wlfareInfoDtlCd 복지정보상세코드

    @Column(nullable = false)
    private String welfareInfoDetailLink; // wlfareInfoReldCn 복지정보관련내용

    @Column(nullable = false)
    private String welfareInfoDetailName; // wlfareInfoReldNm 복지정보관련명
}
