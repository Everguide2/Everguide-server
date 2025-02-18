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

    @Transactional
    //@Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시에 실행
    @Scheduled(cron = "0 0/1 * * * *") // 1분마다 (테스트용)

    public void sendDeadlineNotifications() {
        log.info("[Scheduler] 마감 임박 알림 스케줄러 작동");

        LocalDate today = LocalDate.now();
        List<Bookmark> bookmarks = bookmarkRepository.findAll();

        for (Bookmark bookmark : bookmarks) {
            LocalDate deadline = null;

            if (bookmark.getType() == BookmarkType.JOB && bookmark.getJob() != null) {
                Job job = bookmark.getJob();
                deadline = job.getEndDate();
            } else if (bookmark.getType() == BookmarkType.EDUCATION && bookmark.getEducation() != null) {
                Education education  = bookmark.getEducation();
                deadline = education.getDesignatedPeriodEndDate();
            }

            if (deadline == null || deadline.isBefore(today)) continue;
            long daysLeft = today.until(deadline).getDays();

            if (daysLeft > 7) continue; // 7일 이하만 알림 생성

            String color = (daysLeft <= 3) ? "RED" : "BLACK";

            NotifyType notifyType = getNotifyType(daysLeft);
            if (notifyType == null) continue;

            notificationService.createNotification(
                    bookmark.getMember(),
                    notifyType,
                    "[마감 임박] " + bookmark.getType().name(),
                    "마감까지 " + daysLeft + "일 남았습니다.",
                    color,
                    bookmark.getJob(),
                    bookmark.getEducation()
            );
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
