package com.example.everguide.repository;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.QJob;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.Region;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomJobRepositoryImpl implements CustomJobRepository {
    private final JPAQueryFactory jpaQueryFactory; //쿼리 자동생성
    private final QJob job = QJob.job;


    @Override
    public List<Job> findJobList(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();

    //필터링 조건 추가
        //지역
        if (regionList != null && regionList.size() > 0) {
            predicate.and(job.region.in(regionList));
        }

        // 접수 중 여부
        if (isRecruiting != null) {
            if (isRecruiting) {
                predicate.and(job.hireType.eq(HireType.RECRUITING)); //접수중인 것만 가져오기
            } //접수중인 것만 조회하는 경우

        }
        // 쿼리 작성 + 위의 필터링 조건 적용
        JPQLQuery<Job> query =  jpaQueryFactory.selectFrom(job).where(predicate);

    //정렬 조건 추가
        if (sortBy != null) {
            if (sortBy.equals("startDate")) { //시작일 정렬
                query.orderBy(job.startDate.asc()); //일단 오름차순으로
            }else if(sortBy.equals("endDate")) { //마감일 정렬
                query.orderBy(job.endDate.asc()); //일단 오름차순으로
            }
        }

    //쿼리에 페이징 설정 추가
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        return  query.fetch();
    }
}
