package com.example.everguide.service.bookmark;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.BookmarkType;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.web.dto.bookmark.BookmarkRequestDTO;
import com.example.everguide.web.dto.bookmark.BookmarkResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<BookmarkResponseDTO> getBookmarksByMemberId(Long memberId, Pageable pageable) {
        return bookmarkRepository.findByMemberId(memberId, pageable)
                .map(BookmarkResponseDTO::fromEntity);
    }


    @Transactional(readOnly = true)
    public BookmarkResponseDTO getBookmarkDetail(Long memberId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndMemberId(bookmarkId, memberId)
                .orElseThrow(() -> new RuntimeException("해당 북마크를 찾을 수 없습니다."));
        return BookmarkResponseDTO.fromEntity(bookmark);
    }

    @Transactional
    public BookmarkResponseDTO addBookmark(Long memberId, BookmarkRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Bookmark bookmark = new Bookmark();
        bookmark.setMember(member);
        bookmark.setType(request.getType());

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return BookmarkResponseDTO.fromEntity(savedBookmark);
    }

    @Transactional
    public void deleteBookmark(Long memberId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndMemberId(bookmarkId, memberId)
                .orElseThrow(() -> new RuntimeException("해당 북마크를 찾을 수 없습니다."));
        bookmarkRepository.delete(bookmark);
    }
}
