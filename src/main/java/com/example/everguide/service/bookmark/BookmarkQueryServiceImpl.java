package com.example.everguide.service.bookmark;

import com.example.everguide.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkQueryServiceImpl implements BookmarkQueryService {

    private final BookmarkRepository bookmarkRepository;
} 