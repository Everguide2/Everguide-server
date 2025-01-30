package com.example.everguide.service.bookmark;

import com.example.everguide.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkCommandServiceImpl implements BookmarkCommandService {

    private final BookmarkRepository bookmarkRepository;
} 