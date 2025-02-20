package com.example.everguide.web.controller;

import com.example.everguide.domain.Member;
import com.example.everguide.service.mypage.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/member/mypage") 
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    @GetMapping
    public ResponseEntity<Member> getMypageInfo() {
        return ResponseEntity.ok(mypageService.getMypageInfo());
    }

    @PutMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(mypageService.updateProfileImage(file));
    }
}
