package com.example.everguide.service.recommend;

import com.example.everguide.web.dto.recommend.RecommendRequest;
import com.example.everguide.web.dto.recommend.RecommendResponse;

import java.util.List;

public interface RecommendService {

    Boolean registerSurvey(RecommendRequest.SurveyDTO surveyDTO);

    List<RecommendResponse.RecommendDTO> welfareRecommend();

    RecommendResponse.RecommendDetailsDTO welfareRecommendDetails(Long welfareId);
}