package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          //pk id

    private String educationKey1; //eduCrseNo 접수과정 번호
    private String educationKey2; // lctreNo 강좌번호

    private LocalDate startDate;// 강좌 시작일

    private LocalDate endDate;// 강좌 종료일

    private String eduName;// 강좌명

    private String howTo; //신청방법

}
