package com.example.everguide.repository;

import com.example.everguide.domain.WelfareService;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.survey.HouseholdType;
import com.example.everguide.domain.enums.survey.SupportType;

import java.util.List;
import java.util.Set;

public interface WelfareServiceRepositoryCustom {

    List<WelfareService> welfareServiceSearch(Region region, Set<SupportType> supportTypes, Set<HouseholdType> householdTypes);
}
