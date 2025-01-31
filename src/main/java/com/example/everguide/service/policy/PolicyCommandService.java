package com.example.everguide.service.policy;

import com.example.everguide.web.dto.policy.PolicyApiRequest;

public interface PolicyCommandService {

    /**
     * API로부터 정책 데이터를 조회하여 DB에 저장합니다.
     * 이미 존재하는 정책의 경우 lastModifiedDate를 비교하여 업데이트 여부를 결정합니다.
     *
     * @param request API 요청에 필요한 파라미터를 담은 객체
     */
    void savePolicies(PolicyApiRequest request);
}