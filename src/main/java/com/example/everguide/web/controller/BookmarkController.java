package com.example.everguide.web.controller;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.service.bookmark.BookmarkService;
import com.example.everguide.web.dto.bookmark.BookmarkRequestDTO;
import com.example.everguide.web.dto.bookmark.BookmarkResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarks(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(bookmarkService.getBookmarksByMemberId(memberId));
    }

    @PostMapping
    public ResponseEntity<BookmarkResponseDTO> addBookmark(@AuthenticationPrincipal Long memberId, @RequestBody BookmarkRequestDTO request) {
        return ResponseEntity.ok(bookmarkService.addBookmark(memberId, request));
    }

    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Void> deleteBookmark(@AuthenticationPrincipal Long memberId, @PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(memberId, bookmarkId);
        return ResponseEntity.noContent().build();
    }
}
