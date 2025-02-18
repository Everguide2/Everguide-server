package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.Region;
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
    private Region region;//지역

    private String organName;//기업명

    private LocalDate startDate;//시작 접수일

    private LocalDate EndDate;// 종료 점수일





}