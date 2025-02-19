package com.example.everguide.service.notification;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.enums.BookmarkType;
import com.example.everguide.domain.enums.NotifyType;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.web.notification.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final BookmarkRepository bookmarkRepository;

    @Scheduled(cron = "0 0/1 * * * *") // 1분마다 실행 (테스트용)
    @Transactional
    public void sendDeadlineNotifications() {
        log.info("[Scheduler] 마감 임박 알림 스케줄러 작동");

        LocalDate today = LocalDate.now();
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        log.info("가져온 bookmark 수: {}", bookmarks.size());

        for (Bookmark bookmark : bookmarks) {
            log.info("Bookmark ID: {}, 타입: {}", bookmark.getId(), bookmark.getType());
            LocalDate deadline = null;

            if (bookmark.getType() == BookmarkType.JOB && bookmark.getJob() != null) {
                deadline = bookmark.getJob().getEndDate();
            } else if (bookmark.getType() == BookmarkType.EDUCATION && bookmark.getEducation() != null) {
                deadline = bookmark.getEducation().getDesignatedPeriodEndDate();
            }

            log.info("Bookmark ID: {} deadline: {}", bookmark.getId(), deadline);

            if (deadline == null || deadline.isBefore(today)) {
                log.info("Bookmark ID: {} 조건 미충족: deadline이 null이거나 오늘보다 이전", bookmark.getId());
                continue;
            }

            long daysLeft = today.until(deadline).getDays();
            log.info("Bookmark ID: {} 남은 일수: {}", bookmark.getId(), daysLeft);

            if (daysLeft > 7) {
                log.info("Bookmark ID: {} 조건 미충족: 남은 일수 {}가 7일보다 많음", bookmark.getId(), daysLeft);
                continue;
            }

            NotifyType notifyType = getNotifyType(daysLeft);
            if (notifyType == null) {
                log.info("Bookmark ID: {} 조건 미충족: notifyType이 null", bookmark.getId());
                continue;
            }

            try {
                log.info("Bookmark ID: {} 알림 생성 시도", bookmark.getId());
                notificationService.createNotification(
                        bookmark.getMember(),
                        notifyType,
                        "[마감 임박] " + bookmark.getType().name(),
                        "마감까지 " + daysLeft + "일 남았습니다.",
                        (daysLeft <= 3) ? "RED" : "BLACK",
                        bookmark.getJob(),
                        bookmark.getEducation()
                );
                log.info("Bookmark ID: {} 알림 생성 성공", bookmark.getId());
            } catch (Exception e) {
                log.error("Bookmark ID: {} 알림 생성 실패: {}", bookmark.getId(), e.getMessage());
            }
        }
    }

    private NotifyType getNotifyType(long daysLeft) {
        return switch ((int) daysLeft) {
            case 7 -> NotifyType.DEADLINE_D7;
            case 6 -> NotifyType.DEADLINE_D6;
            case 5 -> NotifyType.DEADLINE_D5;
            case 4 -> NotifyType.DEADLINE_D4;
            case 3 -> NotifyType.DEADLINE_D3;
            case 2 -> NotifyType.DEADLINE_D2;
            case 1 -> NotifyType.DEADLINE_D1;
            case 0 -> NotifyType.DEADLINE_DAY;
            default -> null;
        };
    }
}