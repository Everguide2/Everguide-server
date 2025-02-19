package com.example.everguide.web.controller;

import com.example.everguide.service.bookmark.BookmarkService;
import com.example.everguide.web.dto.bookmark.BookmarkRequestDTO;
import com.example.everguide.web.dto.bookmark.BookmarkResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    public ResponseEntity<Page<BookmarkResponseDTO>> getBookmarks(@AuthenticationPrincipal Long memberId,
                                                                  @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookmarkService.getBookmarksByMemberId(memberId, pageable));
    }


    @GetMapping("/{bookmarkId}")
    public ResponseEntity<BookmarkResponseDTO> getBookmarkDetail(@AuthenticationPrincipal Long memberId,
                                                                 @PathVariable Long bookmarkId) {
        return ResponseEntity.ok(bookmarkService.getBookmarkDetail(memberId, bookmarkId));
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
