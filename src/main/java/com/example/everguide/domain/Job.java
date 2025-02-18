package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.RegionDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobCode; //직업 코드

    private String name;//채용 제목

    @Enumerated(EnumType.STRING)
    private HireType hireType; //마감여부

    @Enumerated(EnumType.STRING)
    private Region region;//지역 시

    private RegionDetail regionDetail; //상세 지역 (구, 군 .동. 등...)  REGION_010090("노원구") 형식

    private String organName;//기업명

    private LocalDate startDate;//시작 접수일

    private LocalDate endDate;// 종료 점수일





}

