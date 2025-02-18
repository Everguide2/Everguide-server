package com.example.everguide.service.notification;

import com.example.everguide.domain.Bookmark;
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

            // JOB인지 EDUCATION인지에 따라 마감일 가져오기
            if (bookmark.getType() == BookmarkType.JOB && bookmark.getJob() != null) {
                Job job = bookmark.getJob();
                deadline = job.getEndDate();
            } else if (bookmark.getType() == BookmarkType.EDUCATION && bookmark.getEducation() != null) {
                deadline = bookmark.getEducation().getDesignatedPeriodEndDate();
            }

            // 마감일이 없거나 이미 지났다면 알림 생성 X
            if (deadline == null) continue;
            long daysLeft = today.until(deadline).getDays();
            if (daysLeft < 0) continue;

            // 남은 일 수에 따라 color 결정: 3일 이하 → RED, 그 외 → BLACK
            String color = (daysLeft <= 3) ? "RED" : "BLACK";

            // notifyType을 더 세분화하고 싶으면 아래처럼 switch/case 로직 사용 가능
            // 여기서는 예시로 D-7, D-3, D-2, D-1, D-Day 중 맞으면 쓰도록, 아니면 null
            // 필요 없다면 고정된 값(예: NotifyType.DEADLINE 등)도 가능
            NotifyType notifyType = getNotifyType(daysLeft);
            if (notifyType == null) {
                // 예: “7,3,2,1,0 아니면 알림 필요 없다”는 식이면 continue
                // 단순히 무조건 알림을 만들고 싶으면 null 대신 임의의 NotifyType (e.g. DEADLINE_OTHERS) 할당도 가능
                continue;
            }

            notificationService.createNotification(
                    bookmark.getMember(),
                    notifyType,
                    "[마감 임박] " + bookmark.getType().name(), // JOB or EDUCATION
                    "마감까지 " + daysLeft + "일 남았습니다.",
                    color,
                    bookmark.getJob(),
                    bookmark.getEducation()
            );
        }
    }

    // 필요하다면 기존 DEADLINE_D7, D3, D2, D1, DAY를 쓰는 로직
    private NotifyType getNotifyType(long daysLeft) {
        return switch ((int) daysLeft) {
            case 7 -> NotifyType.DEADLINE_D7;
            case 3 -> NotifyType.DEADLINE_D3;
            case 2 -> NotifyType.DEADLINE_D2;
            case 1 -> NotifyType.DEADLINE_D1;
            case 0 -> NotifyType.DEADLINE_DAY;
            default -> null;
        };
    }
}
