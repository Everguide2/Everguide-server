package com.example.everguide.web.dto.bookmark;

import com.example.everguide.domain.enums.BookmarkType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequestDTO {
    private BookmarkType type;
    private Long targetId;
}