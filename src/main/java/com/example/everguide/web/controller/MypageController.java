package com.example.everguide.web.controller;

import com.example.everguide.domain.Member;
import com.example.everguide.service.mypage.MypageService;
import com.example.everguide.web.dto.mypage.PasswordUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    @GetMapping
    public ResponseEntity<Member> getMypageInfo(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(mypageService.getMypageInfo(memberId));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfileImage(
            @AuthenticationPrincipal Long memberId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(mypageService.updateProfileImage(memberId, file));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal Long memberId,
            @RequestBody PasswordUpdateRequest request) {
        mypageService.updatePassword(memberId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal Long memberId) {
        mypageService.deleteAccount(memberId);
        return ResponseEntity.noContent().build();
    }
}
