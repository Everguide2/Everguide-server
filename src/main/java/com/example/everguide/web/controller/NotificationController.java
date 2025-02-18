//package com.example.everguide.web.notification;
//
//import com.example.everguide.domain.Member;
//import com.example.everguide.domain.Notification;
//import com.example.everguide.web.notification.NotificationService;
//import com.example.everguide.web.notification.dto.NotificationDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * 알림(Notification) 관련 API.
// * 여기서는 회원 정보를 @RequestAttribute("loginMember")로 받지만,
// * 실제 로그인/보안 로직에 따라 @AuthenticationPrincipal, 세션, JWT 등을 사용하세요.
// */
//@RestController
//@RequestMapping("/notifications")
//@RequiredArgsConstructor
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    /**
//     * 회원의 전체 알림 목록 조회
//     */
//    @GetMapping
//    public List<NotificationDto> getNotifications(@RequestAttribute("loginMember") Member member) {
//        List<Notification> notifications = notificationService.getNotificationsForMember(member);
//        return notifications.stream()
//                .map(NotificationDto::fromEntity)
//                .toList();
//    }
//
//    /**
//     * 회원의 읽지 않은 알림 목록 조회
//     */
//    @GetMapping("/unread")
//    public List<NotificationDto> getUnreadNotifications(@RequestAttribute("loginMember") Member member) {
//        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(member);
//        return unreadNotifications.stream()
//                .map(NotificationDto::fromEntity)
//                .toList();
//    }
//
//    /**
//     * 알림 읽음 처리
//     */
//    @PostMapping("/{notificationId}/read")
//    public void markAsRead(@RequestAttribute("loginMember") Member member,
//                           @PathVariable Long notificationId) {
//        // 실제로는 "notification이 해당 member의 것인지"도 검증 필요
//        notificationService.markAsRead(notificationId);
//    }
//}

package com.example.everguide.web.controller;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.Notification;
//import com.example.everguide.web.notification.NotificationService;
//import com.example.everguide.web.notification.dto.NotificationDto;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.notification.NotificationService;
import com.example.everguide.web.dto.notificaton.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림(Notification) 관련 API
 * - 여기서는 로그인 로직 없이, memberId를 쿼리 파라미터로 받아 테스트용으로 사용
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    // ↑ Member를 찾기 위해 필요 (프로젝트에 맞게 실제 MemberRepository 경로/이름 확인)

    /**
     * 회원의 전체 알림 목록 조회
     * 예: GET /notifications?memberId=1
     */
    @GetMapping
    public List<NotificationDto> getNotifications(@RequestParam("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        List<Notification> notifications = notificationService.getNotificationsForMember(member);
        return notifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * 회원의 '읽지 않은' 알림 목록 조회
     * 예: GET /notifications/unread?memberId=1
     */
    @GetMapping("/unread")
    public List<NotificationDto> getUnreadNotifications(@RequestParam("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(member);
        return unreadNotifications.stream()
                .map(NotificationDto::fromEntity)
                .toList();
    }

    /**
     * 알림 읽음 처리
     * 예: POST /notifications/9001/read?memberId=1
     */
    @PostMapping("/{notificationId}/read")
    public void markAsRead(@RequestParam("memberId") Long memberId,
                           @PathVariable Long notificationId) {
        // 실제로는 "notification이 해당 member의 소유인지" 확인하는 로직 필요
        notificationService.markAsRead(notificationId);
    }

//    @GetMapping("/welcome")
//    public String sendWelcome(@RequestParam("memberId") Long memberId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));
//        notificationService.sendWelcomeNotification(member);
//        return "Welcome notification sent.";
//    }
}

