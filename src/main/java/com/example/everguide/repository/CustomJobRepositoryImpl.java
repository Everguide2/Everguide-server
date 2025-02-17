package com.example.everguide.repository;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.QJob;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.Region;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Slf4j
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
        if (regionList != null && !regionList.isEmpty()) {
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



    //모집 기한이 이번주에 끝나는 일자리 정보를 리스트로 배열 + d-day 순으로 정렬
    @Override
    public List<Job> findThisWeekJobList() {
        LocalDate today = LocalDate.now(); // 오늘 날짜
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY); //이번 주 월요일
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY); //이번 주 일요일

        return jpaQueryFactory.selectFrom(job)
                .where(job.endDate.between(startOfWeek, endOfWeek)) //마감일이 이번주 사이에 있는 일자리 필터링
                .orderBy(job.endDate.asc()) // 종료일(D-day) 순으로 오름차순 정렬 = 마감일이 오늘보다 가까운 것부터 먼 것 순서 - 오름차순
                .limit(7) // 대표 일자리 포함 7개
                .fetch();
    }

}
