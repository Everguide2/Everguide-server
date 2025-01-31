package com.example.everguide.service.policy;

import com.example.everguide.client.PolicyApiClient;
import com.example.everguide.domain.Policy;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.survey.HouseholdType;
import com.example.everguide.domain.enums.survey.LifeCycleType;
import com.example.everguide.domain.enums.survey.SupportType;
import com.example.everguide.repository.PolicyRepository;
import com.example.everguide.web.dto.policy.PolicyApiRequest;
import com.example.everguide.web.dto.policy.PolicyApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PolicyCommandServiceImpl implements PolicyCommandService {

    private final PolicyApiClient policyApiClient;
    private final PolicyRepository policyRepository;

    @Override
    public Mono<Void> fetchAndSavePolicies(PolicyApiRequest request) {
        return policyApiClient.fetchPolicies(request)
                .doOnNext(response -> {
                    if (response == null) {
                        log.error("API response is null");
                        return;
                    }

                    if (!"00".equals(response.getResultCode())) {
                        log.error("API error - code: {}, message: {}", 
                            response.getResultCode(), 
                            response.getResultMessage());
                        return;
                    }

                    List<PolicyApiResponse.PolicyInfo> policies = response.getServList();
                    if (policies == null || policies.isEmpty()) {
                        log.warn("No policies found in the response");
                        return;
                    }

                    log.info("Fetched {} policies", policies.size());
                    
                    policies.forEach(item -> {
                        try {
                            Policy policy = convertToPolicy(item);
                            policyRepository.findByServiceId(policy.getServiceId())
                                    .ifPresentOrElse(
                                            existingPolicy -> {
                                                if (existingPolicy.getLastModifiedDate() != null && 
                                                    policy.getLastModifiedDate() != null && 
                                                    existingPolicy.getLastModifiedDate().isBefore(policy.getLastModifiedDate())) {
                                                    policyRepository.save(policy);
                                                    log.info("Policy updated: {}", policy.getServiceId());
                                                } else {
                                                    log.info("Policy already up to date: {}", policy.getServiceId());
                                                }
                                            },
                                            () -> {
                                                policyRepository.save(policy);
                                                log.info("New policy saved: {}", policy.getServiceId());
                                            }
                                    );
                        } catch (Exception e) {
                            log.error("Error processing policy item: {}", e.getMessage(), e);
                        }
                    });
                })
                .doOnError(error -> log.error("Error fetching policies: {}", error.getMessage(), error))
                .then();
    }

    @Override
    public void savePolicies(PolicyApiRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'savePolicies'");
    }

    private Policy saveOrUpdatePolicy(Policy policy) {
        return policyRepository.findByServiceId(policy.getServiceId())
                .map(existingPolicy -> updateExistingPolicy(existingPolicy, policy))
                .orElse(policy);
    }

    private Policy updateExistingPolicy(Policy existingPolicy, Policy newPolicy) {
        // 여기서는 lastModifiedDate를 비교하여 업데이트가 필요한지 확인
        if (existingPolicy.getUpdatedAt().isBefore(newPolicy.getUpdatedAt())) {
            return policyRepository.save(newPolicy);
        }
        return existingPolicy;
    }

    private Policy convertToPolicy(PolicyApiResponse.PolicyInfo item) {
        return Policy.builder()
                .targetRegion(Region.fromString(item.getCtpvNm()))
                .targetRegionDetail(item.getSggNm())
                .supportTypes(convertToSupportTypes(parseArray(item.getIntrsThemaNmArray())))
                .lifeCycleTypes(convertToLifeCycleTypes(parseArray(item.getLifeNmArray())))
                .applyMethod(item.getAplyMtdNm())
                .chargeDepartment(item.getBizChrDeptNm())
                .serviceDigest(item.getServDgst())
                .serviceDetailLink(item.getServDtlLink())
                .serviceId(item.getServId())
                .serviceName(item.getServNm())
                .supportCycle(item.getSprtCycNm())
                .provisionType(item.getSrvPvsnNm())
                .householdConditions(convertToHouseholdTypes(parseArray(item.getTrgterIndvdlNmArray())))
                .inquiryNumber(parseInteger(item.getInqNum()))
                .lastModifiedDate(parseDateTime(item.getLastModYmd()))
                .build();
    }

    // 문자열로 된 배열을 List<String>으로 변환
    private List<String> parseArray(String arrayStr) {
        if (arrayStr == null || arrayStr.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(arrayStr.split(","));
    }

    // 문자열을 Integer로 변환
    private Integer parseInteger(String value) {
        try {
            return value != null ? Integer.parseInt(value.trim()) : null;
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer value: {}", value);
            return null;
        }
    }

    // 문자열 날짜를 LocalDateTime으로 변환
    private LocalDateTime parseDateTime(String dateStr) {
        try {
            if (dateStr == null || dateStr.length() != 8) {
                return null;
            }
            // yyyyMMdd 형식의 문자열을 LocalDateTime으로 변환
            return LocalDateTime.parse(dateStr,
                    DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse date value: {}", dateStr);
            return null;
        }
    }

    private Set<SupportType> convertToSupportTypes(List<String> supportTypes) {
        if (supportTypes == null) return Collections.emptySet();
        return supportTypes.stream()
                .map(SupportType::fromString)
                .collect(Collectors.toSet());
    }

    private Set<LifeCycleType> convertToLifeCycleTypes(List<String> lifeCycleTypes) {
        if (lifeCycleTypes == null) return Collections.emptySet();
        return lifeCycleTypes.stream()
                .map(LifeCycleType::fromString)
                .collect(Collectors.toSet());
    }

    private Set<HouseholdType> convertToHouseholdTypes(List<String> householdTypes) {
        if (householdTypes == null) return Collections.emptySet();
        return householdTypes.stream()
                .map(HouseholdType::fromString)
                .collect(Collectors.toSet());
    }
}