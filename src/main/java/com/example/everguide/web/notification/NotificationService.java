package com.example.everguide.web.notification;

import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.Notification;
import com.example.everguide.domain.enums.NotifyType;
import com.example.everguide.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 생성 (기존 메서드)
     */
    public Notification createNotification(
            Member member,
            NotifyType type,
            String title,
            String message,
            String color,
            Job job,
            Education education
    ) {
        Notification notification = Notification.builder()
                .member(member)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .color(color)
                .job(job)
                .education(education)
                .build();

        notificationRepository.save(notification);
        cleanUpNotifications(member);
        return notification;
    }

    /**
     * 회원의 전체 알림 목록 조회
     */
    public List<Notification> getNotificationsForMember(Member member) {
        return notificationRepository.findByMemberOrderByCreatedDateDesc(member);
    }

    /**
     * 회원의 '읽지 않은' 알림 목록 조회
     */
    public List<Notification> getUnreadNotifications(Member member) {
        return notificationRepository.findByMemberAndIsReadFalseOrderByCreatedDateDesc(member);
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.markAsRead();
    }

    /**
     * 최대 30개 초과 시, 오래된 알림 삭제
     */
    private void cleanUpNotifications(Member member) {
        // 최신순(내림차순)으로 정렬된 알림 목록 조회
        List<Notification> notifications = notificationRepository.findByMemberOrderByCreatedDateDesc(member);
        if (notifications.size() > 30) {
            // 최신 30개를 제외한 나머지 알림을 삭제
            List<Notification> notificationsToDelete = notifications.subList(30, notifications.size());
            notificationRepository.deleteAllInBatch(notificationsToDelete);
        }
    }

    /**
     * 회원의 생성일(createdDate)이 오늘인 경우, 환영 알람을 보내는 메서드
     */
    public void sendWelcomeNotification(Member member) {
        // Member의 생성일이 오늘이면 환영 알림 생성
        if (member.getCreatedDate() != null &&
                member.getCreatedDate().toLocalDate().isEqual(LocalDate.now())) {
            // 이미 환영 알림이 있는지 확인 (중복 발송 방지)
            List<Notification> existing = notificationRepository.findByMemberAndType(member, NotifyType.WELCOME);
            if (existing == null || existing.isEmpty()) {
                createNotification(
                        member,
                        NotifyType.WELCOME,
                        "[환영합니다!]",
                        "반가워요!! 이제부터 어떻게 everguide를 사용하면 좋을지 소개해 드릴게요:)",
                        "YELLOW", // 가입 인사 메시지는 노란색
                        null,
                        null
                );
            }
        }
    }
}
