package com.example.everguide.service.welfare;

import com.example.everguide.domain.WelfareService;
import com.example.everguide.repository.BookmarkRepository;
import com.example.everguide.repository.WelfareServiceRepository;
import com.example.everguide.web.dto.welfare.WelfareServiceDetailResponse;
import com.example.everguide.web.dto.welfare.WelfareServiceListResponse;
import com.example.everguide.web.dto.welfare.WelfareServiceSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WelfareQueryServiceImpl implements WelfareQueryService {

    private final WelfareServiceRepository welfareServiceRepository;
    private final BookmarkRepository bookmarkRepository;

    @Override
    public Page<WelfareServiceListResponse> searchWelfareServices(WelfareServiceSearchRequest request, Pageable pageable) {
        return welfareServiceRepository.searchWelfareServices(
            request.keyword(),
            request.intrsThemaNmArray(),
            request.ctpvNm(),
            request.lastModYmd(),
            pageable
        ).map(WelfareServiceListResponse::from);
    }

    @Override
    public WelfareServiceDetailResponse getWelfareServiceDetail(String servId, Long memberId) {
        WelfareService welfareService = welfareServiceRepository.findById(servId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 복지 서비스입니다."));

        boolean isBookmarked = false;
        if (memberId != null) {
            isBookmarked = bookmarkRepository.existsByMemberIdAndWelfareServiceId(memberId, servId);
        }

        return WelfareServiceDetailResponse.of(welfareService, isBookmarked);
    }
} 