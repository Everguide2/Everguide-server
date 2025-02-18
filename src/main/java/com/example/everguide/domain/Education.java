package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

public class Education extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer educationCenterId;          //노인교육센터번호
    private LocalDate startDate;// 강좌 시작일
    private LocalDate endDate;// 강좌 종료일
    private String eduName;// 강좌명
    private String CompanyName;// 기관명 <- 더미데이터 기준, 실제이면 신청방법 들어갈 예정
    //
//    private String consignment;                 // 위탁여부
//    private String educationCenterName;         // 교육센터명
//    private String organizationName;            // 기관명
//    private String businessInformation;         // 사업내용
//    private String educationTarget;             // 교육대상
//    private Integer zipCode;                    // 우편번호
//    private String location;                    // 소재지
//    private String contact;                     // 연락처
//    private String designatedPeriodStartDate;   // 지정기간시작일
//    private LocalDate designatedPeriodEndDate;  // 지정기간종료일
}
