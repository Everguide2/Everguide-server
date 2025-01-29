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

public class EducationInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long educationId;

    @Column(nullable = false, length = 10)
    private String applyStartDate;      //접수시작일 acptFrDd

    @Column(length = 10)
    private String applyEndDate;        //접수종료일 acptToDd
    private Integer applyTotalCount;    //신청인원 applyTotcnt

    @Column(length = 20)
    private String area;                //지역 citiprovNo

    @Column(nullable = false)
    private Integer courseNo;           //접수과정번호 eduCrseNo

    @Column(length = 6)
    private String educationTarget;     //대상 eduTgt

    @Column(length = 10)
    private String lectureStartDate;    //강좌시작일 lctreFrDd

    @Column(length = 100)
    private String lectureName;         //강좌명 lctreNm

    @Column(length = 38)
    private Integer lectureNo;          //강좌번호 lctreNo

    @Column(length = 38)
    private Integer lectureSequence;    //회차 lctreSeq

    @Column(length = 5)
    private Integer lectureTime;        //교육시간 lctreTm

    @Column(length = 10)
    private String lectureEndDate;      //강좌종료일 lctreToDd

    @Column(length = 38)
    private Integer participationAmount;//참가비 partcptAmt

    @Column(length = 2000)
    private String requestMethod;       //신청방법 reqMthd

    @Column(length = 1)
    private String stayingYn;           //숙박여부 stayngYn
    private Integer totalAcceptPeople;  //모집인원 totAcptPrnnum
}
