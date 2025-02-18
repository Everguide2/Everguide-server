package com.example.everguide.domain;

import com.example.everguide.domain.common.BaseEntity;
import com.example.everguide.domain.enums.NotifyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 알람을 받는 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 어떤 타입의 알림인지 (D-7, D-3, 정책 추가 알림 등등)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotifyType type;


    // 알림 제목
    private String title;

    // 알림 내용
    @Column(length = 1000)
    private String message;

    // 읽음 여부
    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isRead;


    // 알림 색상 (안읽음: RED, 읽음: GRAY 등)
    private String color;

    // 특정 Job과 연결해야 한다면 (옵션)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = true)
    private Job job;

    // 특정 Education과 연결해야 한다면 (옵션)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_id", nullable = true)
    private Education education;

    public void markAsRead() {
        this.isRead = true;
        this.color = "GRAY"; // 읽은 경우 회색으로 전환
    }
}

