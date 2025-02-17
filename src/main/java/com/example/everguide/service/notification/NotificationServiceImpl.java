package com.example.everguide.service.notification;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.Notification;
import com.example.everguide.domain.enums.NotifyType;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.repository.NotificationRepository;
import com.example.everguide.web.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void createNotification(Long memberId, String title, String content, NotifyType type, String resourceUrl) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        Notification notification = Notification.builder()
                .member(member)
                .title(title)
                .content(content)
                .type(type)
                .resourceUrl(resourceUrl)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getNotifications(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Override
    public long getUnreadNotificationCount(Long memberId) {
        return notificationRepository.countUnreadNotifications(memberId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        notification.markAsRead();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void deleteExpiredNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(now);
        notificationRepository.deleteAll(expiredNotifications);
    }

    @Override
    public List<NotificationResponse> getNotificationsByType(Long memberId, NotifyType type) {
        return notificationRepository.findByMemberIdAndTypeOrderByCreatedAtDesc(memberId, type)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
} 