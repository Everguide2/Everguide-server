package com.example.everguide.repository;

import com.example.everguide.domain.*;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.domain.enums.survey.HouseholdType;
import com.example.everguide.domain.enums.survey.SupportType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class WelfareServiceRepositoryImpl implements WelfareServiceRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QWelfareService welfareService = QWelfareService.welfareService;

    @Override
    public List<WelfareService> welfareServiceSearch(String region, Set<SupportType> supportTypes, Set<HouseholdType> householdTypes) {

        String lifeCycle = "노년";

        return queryFactory
                .selectFrom(welfareService)
                .where(
                        all(lifeCycle, region, supportTypes, householdTypes)
                )
                .fetch();
    }

    public BooleanBuilder all(String lifeCycle, String region, Set<SupportType> supportTypes, Set<HouseholdType> householdTypes) {
        return lifeCycleContains(lifeCycle)
                .and(regionEq(region))
                .and(supportTypesHouseholdTypes(supportTypes, householdTypes));
    }

    public BooleanBuilder supportTypesHouseholdTypes(Set<SupportType> supportTypes, Set<HouseholdType> householdTypes) {
        return supportPhysicalHealthContains(supportTypes)
                .or(supportMentalHealthContains(supportTypes))
                .or(supportLifeSupportContains(supportTypes))
                .or(supportHousingContains(supportTypes))
                .or(supportEmploymentContains(supportTypes))
                .or(supportCultureLeisureContains(supportTypes))
                .or(supportSafetyCrisisContains(supportTypes))
                .or(supportPregnancyBirthContains(supportTypes))
                .or(supportChildcareContains(supportTypes))
                .or(supportEducationContains(supportTypes))
                .or(supportAdoptPosterContains(supportTypes))
                .or(supportProtectionCareContains(supportTypes))
                .or(supportFinanceContains(supportTypes))
                .or(supportLegalContains(supportTypes))
                .or(householdMulticulturalContains(householdTypes))
                .or(householdMultichildContains(householdTypes))
                .or(householdVeteranContains(householdTypes))
                .or(householdDisabledContains(householdTypes))
                .or(householdLowIncomeContains(householdTypes))
                .or(householdSingleGrandParentContains(householdTypes));
    }

    public BooleanBuilder supportTypes(Set<SupportType> supportTypes) {
        return supportPhysicalHealthContains(supportTypes)
                .or(supportMentalHealthContains(supportTypes))
                .or(supportLifeSupportContains(supportTypes))
                .or(supportHousingContains(supportTypes))
                .or(supportEmploymentContains(supportTypes))
                .or(supportCultureLeisureContains(supportTypes))
                .or(supportSafetyCrisisContains(supportTypes))
                .or(supportPregnancyBirthContains(supportTypes))
                .or(supportChildcareContains(supportTypes))
                .or(supportEducationContains(supportTypes))
                .or(supportAdoptPosterContains(supportTypes))
                .or(supportProtectionCareContains(supportTypes))
                .or(supportFinanceContains(supportTypes))
                .or(supportLegalContains(supportTypes));
    }

    public BooleanBuilder householdTypes(Set<HouseholdType> householdTypes) {
        return householdMulticulturalContains(householdTypes)
                .or(householdMultichildContains(householdTypes))
                .or(householdVeteranContains(householdTypes))
                .or(householdDisabledContains(householdTypes))
                .or(householdLowIncomeContains(householdTypes))
                .or(householdSingleGrandParentContains(householdTypes));
    }

    public BooleanBuilder lifeCycleContains(String lifeCycle) {

        return nullSafeBooleanBuilder(() -> welfareService.lifeCycle.contains(lifeCycle));
    }

    public BooleanBuilder regionEq(String region) {

        return nullSafeBooleanBuilder(() -> welfareService.region.eq(region));
    }

    // - - - - -

    public BooleanBuilder supportPhysicalHealthContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.PHYSICAL_HEALTH)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.PHYSICAL_HEALTH.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportMentalHealthContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.MENTAL_HEALTH)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.MENTAL_HEALTH.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportLifeSupportContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.LIFE_SUPPORT)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.LIFE_SUPPORT.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportHousingContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.HOUSING)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.HOUSING.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportEmploymentContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.EMPLOYMENT)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.EMPLOYMENT.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportCultureLeisureContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.CULTURE_LEISURE)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.CULTURE_LEISURE.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportSafetyCrisisContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.SAFETY_CRISIS)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.SAFETY_CRISIS.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportPregnancyBirthContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.PREGNANCY_CHILDBIRTH)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.PREGNANCY_CHILDBIRTH.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportChildcareContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.CHILDCARE)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.CHILDCARE.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportEducationContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.EDUCATION)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.EDUCATION.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportAdoptPosterContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.ADOPT_POSTER)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.ADOPT_POSTER.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportProtectionCareContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.PROTECTION_CARE)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.PROTECTION_CARE.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportFinanceContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.FINANCE)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.FINANCE.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder supportLegalContains(Set<SupportType> supportTypes) {

        if (supportTypes.contains(SupportType.LEGAL)) {
            return nullSafeBooleanBuilder(() -> welfareService.supportTypes.contains(SupportType.LEGAL.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    // - - - - -

    public BooleanBuilder householdMulticulturalContains(Set<HouseholdType> householdTypes) {

        if (householdTypes.contains(HouseholdType.MULTICULTURAL)) {
            return nullSafeBooleanBuilder(() -> welfareService.householdConditions.contains(HouseholdType.MULTICULTURAL.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder householdMultichildContains(Set<HouseholdType> householdTypes) {

        if (householdTypes.contains(HouseholdType.MULTICHILD)) {
            return nullSafeBooleanBuilder(() -> welfareService.householdConditions.contains(HouseholdType.MULTICHILD.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder householdVeteranContains(Set<HouseholdType> householdTypes) {

        if (householdTypes.contains(HouseholdType.VETERAN)) {
            return nullSafeBooleanBuilder(() -> welfareService.householdConditions.contains(HouseholdType.VETERAN.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder householdDisabledContains(Set<HouseholdType> householdTypes) {

        if (householdTypes.contains(HouseholdType.DISABLED)) {
            return nullSafeBooleanBuilder(() -> welfareService.householdConditions.contains(HouseholdType.DISABLED.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder householdLowIncomeContains(Set<HouseholdType> householdTypes) {

        if (householdTypes.contains(HouseholdType.LOW_INCOME)) {
            return nullSafeBooleanBuilder(() -> welfareService.householdConditions.contains(HouseholdType.LOW_INCOME.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    public BooleanBuilder householdSingleGrandParentContains(Set<HouseholdType> householdTypes) {

        if (householdTypes.contains(HouseholdType.SINGLE_PARENT_GRANDPARENT)) {
            return nullSafeBooleanBuilder(() -> welfareService.householdConditions.contains(HouseholdType.SINGLE_PARENT_GRANDPARENT.getDescription()));
        } else {
            return new BooleanBuilder();
        }
    }

    // - - - - -

    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {

        try {
            return new BooleanBuilder(supplier.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}