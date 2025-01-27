package com.example.everguide.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSurvey is a Querydsl query type for Survey
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSurvey extends EntityPathBase<Survey> {

    private static final long serialVersionUID = -1770589789L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSurvey survey = new QSurvey("survey");

    public final com.example.everguide.domain.common.QBaseEntity _super = new com.example.everguide.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<com.example.everguide.domain.enums.survey.DeviceUsage> deviceUsage = createEnum("deviceUsage", com.example.everguide.domain.enums.survey.DeviceUsage.class);

    public final EnumPath<com.example.everguide.domain.enums.survey.DisabilityGrade> disabilityGrade = createEnum("disabilityGrade", com.example.everguide.domain.enums.survey.DisabilityGrade.class);

    public final EnumPath<com.example.everguide.domain.enums.survey.FamilyDistance> familyDistance = createEnum("familyDistance", com.example.everguide.domain.enums.survey.FamilyDistance.class);

    public final SetPath<com.example.everguide.domain.enums.survey.HouseholdType, EnumPath<com.example.everguide.domain.enums.survey.HouseholdType>> householdTypes = this.<com.example.everguide.domain.enums.survey.HouseholdType, EnumPath<com.example.everguide.domain.enums.survey.HouseholdType>>createSet("householdTypes", com.example.everguide.domain.enums.survey.HouseholdType.class, EnumPath.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.example.everguide.domain.enums.survey.IncomeSupport> incomeSupports = createEnum("incomeSupports", com.example.everguide.domain.enums.survey.IncomeSupport.class);

    public final BooleanPath livingAlone = createBoolean("livingAlone");

    public final QMember member;

    public final EnumPath<com.example.everguide.domain.enums.Region> region = createEnum("region", com.example.everguide.domain.enums.Region.class);

    public final SetPath<com.example.everguide.domain.enums.survey.ResourceType, EnumPath<com.example.everguide.domain.enums.survey.ResourceType>> resourceTypes = this.<com.example.everguide.domain.enums.survey.ResourceType, EnumPath<com.example.everguide.domain.enums.survey.ResourceType>>createSet("resourceTypes", com.example.everguide.domain.enums.survey.ResourceType.class, EnumPath.class, PathInits.DIRECT2);

    public final EnumPath<com.example.everguide.domain.enums.survey.SurveyTarget> target = createEnum("target", com.example.everguide.domain.enums.survey.SurveyTarget.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<com.example.everguide.domain.enums.survey.WelfareStatus> welfareStatus = createEnum("welfareStatus", com.example.everguide.domain.enums.survey.WelfareStatus.class);

    public QSurvey(String variable) {
        this(Survey.class, forVariable(variable), INITS);
    }

    public QSurvey(Path<? extends Survey> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSurvey(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSurvey(PathMetadata metadata, PathInits inits) {
        this(Survey.class, metadata, inits);
    }

    public QSurvey(Class<? extends Survey> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

