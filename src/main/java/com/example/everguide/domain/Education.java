package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Education extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer educationCenterId;

    private String consignment;
    private String educationCenterName;
    private String organizationName;
    private String businessInformation;
    private String educationTarget;
    private Integer zipCode;
    private String location;
    private String contact;
    private String designatedPeriodStartDate;
    private LocalDate designatedPeriodEndDate; // 마감일

}
