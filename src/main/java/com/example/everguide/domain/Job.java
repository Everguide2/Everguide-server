package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.JobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobCode;    // API에서 projNo를 받아 저장
    private String companyName; // orgName 기관명
    private Integer projectYear; // prjYear 사업년도
    private String location;    // workPlace 위치
    private String regionSido;  // dstrCd1Nm 시도
    private String regionSigungu; // dstrCd2Nm 시군구
    private String wage;        // intCd1 임금

    @Enumerated(EnumType.STRING)
    private JobType jobType;    // jobType 인턴,연수 여부 [INT, TRN, ALL]

    @Enumerated(EnumType.STRING)
    private HireType hireType; // trnStatNm 구인상태  [PROGRESS, COMPLETE]
    private Integer recruitCnt; // hpIntvCnt 구직인원

    private LocalDate postingStartDate; // hpNotiSdate
    private LocalDate postingEndDate;   // hpNotiEdate
}

