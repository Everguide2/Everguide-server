//package com.example.everguide.web.notification;
//
//import com.example.everguide.domain.Education;
//import com.example.everguide.domain.Job;
//import com.example.everguide.domain.Member;
//import com.example.everguide.domain.Notification;
//import com.example.everguide.domain.enums.NotifyType;
//import com.example.everguide.repository.MemberRepository;
//import com.example.everguide.repository.NotificationRepository;
//import jakarta.persistence.EntityNotFoundException;  // import 추가
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class NotificationService {
//
//    private final NotificationRepository notificationRepository;
//    private final MemberRepository memberRepository;  // MemberRepository 주입
//
//    // 수정 이유:
//    // MemberRepository는 static 메서드가 아니므로 인스턴스를 통해 메서드 호출.
//    // EntityNotFoundException import 추가하여 예외 처리 완벽히 함.
//
//    public List<Notification> getNotificationsForUserId(String userId) {
//        Member member = memberRepository.findByUserId(userId)  // static 아님
//                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId: " + userId));
//        return notificationRepository.findByMemberOrderByCreatedDateDesc(member);
//    }
//
//    public List<Notification> getUnreadNotificationsForUserId(String userId) {
//        Member member = memberRepository.findByUserId(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId: " + userId));
//        return notificationRepository.findByMemberAndIsReadFalseOrderByCreatedDateDesc(member);
//    }
//
//    public void markAsReadForUser(Long notificationId, String userId) {
//        Member member = memberRepository.findByUserId(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId: " + userId));
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new RuntimeException("Notification not found"));
//
//        if (!notification.getMember().equals(member)) {
//            throw new RuntimeException("This notification does not belong to the current user");
//        }
//
//        notification.markAsRead();
//    }
//
//    // 기존 코드 유지
//    public Notification createNotification(
//            Member member,
//            NotifyType type,
//            String title,
//            String message,
//            String color,
//            Job job,
//            Education education
//    ) {
//        Notification notification = Notification.builder()
//                .member(member)
//                .type(type)
//                .title(title)
//                .message(message)
//                .isRead(false)
//                .color(color)
//                .job(job)
//                .education(education)
//                .build();
//
//        notificationRepository.save(notification);
//        cleanUpNotifications(member);
//        return notification;
//    }
//
//    public List<Notification> getNotificationsForMember(Member member) {
//        return notificationRepository.findByMemberOrderByCreatedDateDesc(member);
//    }
//
//    public List<Notification> getUnreadNotifications(Member member) {
//        return notificationRepository.findByMemberAndIsReadFalseOrderByCreatedDateDesc(member);
//    }
//
//    public void markAsRead(Long notificationId) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new RuntimeException("Notification not found"));
//        notification.markAsRead();
//    }
//
//    private void cleanUpNotifications(Member member) {
//        List<Notification> notifications = notificationRepository.findByMemberOrderByCreatedDateDesc(member);
//        if (notifications.size() > 30) {
//            List<Notification> notificationsToDelete = notifications.subList(30, notifications.size());
//            notificationRepository.deleteAllInBatch(notificationsToDelete);
//        }
//    }
//
//    public void sendWelcomeNotification(Member member) {
//        if (member.getCreatedDate() != null &&
//                member.getCreatedDate().toLocalDate().isEqual(LocalDate.now())) {
//            List<Notification> existing = notificationRepository.findByMemberAndType(member, NotifyType.WELCOME);
//            if (existing == null || existing.isEmpty()) {
//                createNotification(
//                        member,
//                        NotifyType.WELCOME,
//                        "[환영합니다!]",
//                        "반가워요!! 이제부터 어떻게 everguide를 사용하면 좋을지 소개해 드릴게요:)",
//                        "YELLOW",
//                        null,
//                        null
//                );
//            }
//        }
//    }
//}

package com.example.everguide.web.notification;

import com.example.everguide.domain.*;
import com.example.everguide.domain.enums.NotifyType;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final MemberRepository memberRepository;



    // 회원가입 후 첫 로그인 시 환영 알람 전송
    public void sendWelcomeNotification(Member member) {
        Notification welcomeNotification = Notification.createNotification(
                member,
                NotifyType.WELCOME,
                "[환영합니다!]",
                "반가워요!! 이제부터 Everguide를 어떻게 사용하면 좋을지 소개해 드릴게요! 😊",
                "YELLOW",
                null,
                null
        );
        notificationRepository.save(welcomeNotification);
    }

    public List<Notification> getNotificationsForUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId: " + userId));
        return notificationRepository.findByMemberOrderByCreatedDateDesc(member);
    }

    public List<Notification> getUnreadNotificationsForUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId: " + userId));
        return notificationRepository.findByMemberAndIsReadFalseOrderByCreatedDateDesc(member);
    }

    public void markAsReadForUser(Long notificationId, String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId: " + userId));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getMember().equals(member)) {
            throw new RuntimeException("This notification does not belong to the current user");
        }

        notification.markAsRead();
    }

    public Notification createNotification(
            Member member,
            NotifyType type,
            String title,
            String message,
            String color,
            Job job,
            Education education
    ) {
        Notification notification = Notification.createNotification(member, type, title, message, color, job, education);
        notificationRepository.save(notification);
        cleanUpNotifications(member);
        return notification;
    }


    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.markAsRead();
    }

    private void cleanUpNotifications(Member member) {
        List<Notification> notifications = notificationRepository.findByMemberOrderByCreatedDateDesc(member);
        if (notifications.size() > 30) {
            List<Notification> notificationsToDelete = notifications.subList(30, notifications.size());
            notificationRepository.deleteAllInBatch(notificationsToDelete);
        }
    }
}
