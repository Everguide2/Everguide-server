package com.example.everguide.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJob is a Querydsl query type for Job
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJob extends EntityPathBase<Job> {

    private static final long serialVersionUID = -60908012L;

    public static final QJob job = new QJob("job");

    public final com.example.everguide.domain.common.QBaseEntity _super = new com.example.everguide.domain.common.QBaseEntity(this);

    public final StringPath companyName = createString("companyName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<com.example.everguide.domain.enums.HireType> hireType = createEnum("hireType", com.example.everguide.domain.enums.HireType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath jobCode = createString("jobCode");

    public final EnumPath<com.example.everguide.domain.enums.JobType> jobType = createEnum("jobType", com.example.everguide.domain.enums.JobType.class);

    public final DatePath<java.time.LocalDate> postingEndDate = createDate("postingEndDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> postingStartDate = createDate("postingStartDate", java.time.LocalDate.class);

    public final NumberPath<Integer> projectYear = createNumber("projectYear", Integer.class);

    public final NumberPath<Integer> recruitCnt = createNumber("recruitCnt", Integer.class);

    public final StringPath regionSido = createString("regionSido");

    public final StringPath regionSigungu = createString("regionSigungu");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> wage = createNumber("wage", Integer.class);

    public final StringPath workPlace = createString("workPlace");

    public QJob(String variable) {
        super(Job.class, forVariable(variable));
    }

    public QJob(Path<? extends Job> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJob(PathMetadata metadata) {
        super(Job.class, metadata);
    }

}

