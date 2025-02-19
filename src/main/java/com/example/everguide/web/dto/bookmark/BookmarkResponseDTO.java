package com.example.everguide.web.dto.bookmark;

import com.example.everguide.domain.Bookmark;
import com.example.everguide.domain.enums.BookmarkType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookmarkResponseDTO {
    private Long id;
    private BookmarkType type;
    private String title;
    private LocalDateTime savedAt;

    public static BookmarkResponseDTO fromEntity(Bookmark bookmark) {
        BookmarkResponseDTO dto = new BookmarkResponseDTO();
        dto.setId(bookmark.getId());
        dto.setType(bookmark.getType());
        dto.setSavedAt(bookmark.getCreatedAt());

        if (bookmark.getJob() != null) {
            dto.setTitle(bookmark.getJob().getName()); // 기존 getTitle() → getName()
        } else if (bookmark.getEducation() != null) {
            dto.setTitle(bookmark.getEducation().getEducationCenterName()); // 기존 getTitle() → getEducationCenterName()
        }

        return dto;
    }

}
