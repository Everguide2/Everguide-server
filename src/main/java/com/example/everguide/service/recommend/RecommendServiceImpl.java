package com.example.everguide.service.recommend;

import com.example.everguide.domain.Member;
import com.example.everguide.domain.Survey;
import com.example.everguide.domain.WelfareService;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.survey.DisabilityGrade;
import com.example.everguide.domain.enums.survey.HouseholdType;
import com.example.everguide.domain.enums.survey.SupportType;
import com.example.everguide.domain.enums.survey.SurveyTarget;
import com.example.everguide.jwt.SecurityUtil;
import com.example.everguide.repository.MemberRepository;
import com.example.everguide.repository.SurveyRepository;
import com.example.everguide.repository.WelfareServiceRepository;
import com.example.everguide.web.dto.recommend.RecommendRequest;
import com.example.everguide.web.dto.recommend.RecommendResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendServiceImpl implements RecommendService {

    private final SurveyRepository surveyRepository;
    private final SecurityUtil securityUtil;
    private final MemberRepository memberRepository;
    private final WelfareServiceRepository welfareServiceRepository;

    @Override
    @Transactional
    public Boolean registerSurvey(RecommendRequest.SurveyDTO surveyDTO) {

        String currentUserId = securityUtil.getCurrentUserId();
        Member member = memberRepository.findByUserId(currentUserId).orElseThrow(EntityNotFoundException::new);

        SurveyTarget surveyTarget = SurveyTarget.valueOf(surveyDTO.getSurveyTarget());

        Region region = Region.valueOf(surveyDTO.getRegion());

        List<String> supportTypesList  = surveyDTO.getSupportTypes();
        Set<SupportType> supportTypes = new HashSet<>();
        for (String supportType : supportTypesList) {
            supportTypes.add(SupportType.valueOf(supportType));
        }

        List<String> householdTypesList = surveyDTO.getHouseholdTypes();
        Set<HouseholdType> householdTypes = new HashSet<>();
        for (String householdType : householdTypesList) {
            householdTypes.add(HouseholdType.valueOf(householdType));
        }

        DisabilityGrade disabilityGrade = DisabilityGrade.valueOf(surveyDTO.getDisabilityGrade());

        Survey survey = surveyRepository.findByUserId(currentUserId);
        if (survey == null) {
            survey = Survey.builder()
                    .member(member)
                    .surveyTarget(surveyTarget)
                    .region(region)
                    .supportTypes(supportTypes)
                    .householdTypes(householdTypes)
                    .disabilityGrade(disabilityGrade)
                    .build();
        } else {
            survey.setSurveyTarget(surveyTarget);
            survey.setRegion(region);
            survey.setSupportTypes(supportTypes);
            survey.setHouseholdTypes(householdTypes);
            survey.setDisabilityGrade(disabilityGrade);
        }

        surveyRepository.save(survey);

        return true;
    }

    @Override
    public List<RecommendResponse.RecommendDTO> welfareRecommend() {

        String currentUserId = securityUtil.getCurrentUserId();
        Survey survey = surveyRepository.findByUserId(currentUserId);

        String region = survey.getRegion().getDescription();
        Set<SupportType> supportTypes = survey.getSupportTypes();
        Set<HouseholdType> householdTypes = survey.getHouseholdTypes();

        List<WelfareService> wholeWelfareServiceList = welfareServiceRepository.welfareServiceSearch(region, supportTypes, householdTypes);

        List<WelfareService> welfareServiceList = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        int range = wholeWelfareServiceList.size();

        if (range <= 6) {
            welfareServiceList = wholeWelfareServiceList;
        } else {
            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(range);
                welfareServiceList.add(wholeWelfareServiceList.get(index));
            }
        }

        return welfareServiceList.stream()
                .map(welfareService -> RecommendResponse.RecommendDTO.builder()
                        .id(welfareService.getId())
                        .serviceId(welfareService.getServiceId())
                        .serviceName(welfareService.getServiceName()) // 서비스명
                        .applyMethod(welfareService.getApplyMethod()) // 신청방법명
                        .chargeDepartment(welfareService.getChargeDepartment()) // 사업담당부서명
                        .supportCycle(welfareService.getSupportCycle()) // 지원주기명
                        .provisionType(welfareService.getProvisionType()) // 제공유형명
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public RecommendResponse.RecommendDetailsDTO welfareRecommendDetails(Long welfareId) {

        WelfareService welfareService = welfareServiceRepository.findById(welfareId).orElseThrow(EntityNotFoundException::new);

        return RecommendResponse.RecommendDetailsDTO.builder()
                .id(welfareService.getId())
                .serviceId(welfareService.getServiceId())
                .serviceName(welfareService.getServiceName())
                .serviceDigest(welfareService.getServiceDigest())
                .serviceDetailLink(welfareService.getServiceDetailLink())
                .applyMethod(welfareService.getApplyMethod())
                .chargeDepartment(welfareService.getChargeDepartment())
                .region(welfareService.getRegion())
                .supportTypes(welfareService.getSupportTypes())
                .supportCycle(welfareService.getSupportCycle())
                .provisionType(welfareService.getProvisionType())
                .householdConditions(welfareService.getHouseholdConditions())
                .regionDetail(welfareService.getRegionDetail())
                .build();
    }
}