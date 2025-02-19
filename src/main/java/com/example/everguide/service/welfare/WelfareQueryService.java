package com.example.everguide.service.welfare;

import com.example.everguide.web.dto.welfare.WelfareServiceDetailResponse;
import com.example.everguide.web.dto.welfare.WelfareServiceListResponse;
import com.example.everguide.web.dto.welfare.WelfareServiceSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WelfareQueryService {
    Page<WelfareServiceListResponse> searchWelfareServices(WelfareServiceSearchRequest request, Pageable pageable);
    WelfareServiceDetailResponse getWelfareServiceDetail(String servId, Long memberId);
} 