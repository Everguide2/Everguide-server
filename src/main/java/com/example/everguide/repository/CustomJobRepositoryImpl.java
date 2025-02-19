package com.example.everguide.repository;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.QBookmark;
import com.example.everguide.domain.QJob;
import com.example.everguide.domain.enums.HireType;
import com.example.everguide.domain.enums.Region;
import com.example.everguide.jwt.SecurityUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final QBookmark bookmark = QBookmark.bookmark;


    public Slice<Job> searchJobListByName(String name, Pageable pageable) {
        JPQLQuery<Job> query = jpaQueryFactory.selectFrom(job);

        // name이 null이 아니고 비어 있지 않으면 검색 조건 추가
        if (name != null && !name.trim().isEmpty()) {
            query.where(job.name.containsIgnoreCase(name)); // 이름으로 검색 (대소문자 구분 x)
        }
        query.orderBy(job.endDate.asc()); // 마감일 오름차순 정렬

        // 페이징 처리된 데이터 리스트
        List<Job> jobs = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // +1 개 가져와서 다음 페이지 여부 확인
                .fetch();

        boolean hasNext = jobs.size() > pageable.getPageSize();
        if (hasNext) {
            jobs.remove(jobs.size() - 1);
        }

        // Slice 객체로 반환
        return new SliceImpl<>(jobs, pageable, hasNext);
    }


    //로그인 한 경우
    @Override
    public List<Job> findJobList(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable, Member member) {
        BooleanBuilder predicate = new BooleanBuilder();

    // 필터링 조건 추가
        // 지역
        if (regionList != null && !regionList.isEmpty()) {
            predicate.and(job.region.in(regionList)); // regionList에 포함된 지역에 해당하는 일자리만 필터링
        }

        // 접수 중 여부 (isRecruiting 필터링)
        if (isRecruiting != null) {
            if (isRecruiting) {
                predicate.and(job.hireType.eq(HireType.RECRUITING)); // 접수중인 일자리만 필터링
            }
        }



    // 정렬 조건 추가
        // 북마크 여부 (북마크가 있으면 true, 없으면 false로 표현)
        JPQLQuery<Job> query = jpaQueryFactory.selectFrom(job)
                .leftJoin(bookmark).on(bookmark.job.eq(job).and(bookmark.member.eq(member))) // 북마크된 직업 여부 확인
                .where(predicate)
                .orderBy(
                        bookmark.id.desc().nullsLast() // 북마크 여부를 기준으로 정렬
                );

        if (sortBy != null) {
            if (sortBy.equals("startDate")) { // 시작일 정렬
                query.orderBy(job.startDate.asc()); // 시작일 오름차순 정렬
            } else if (sortBy.equals("endDate")) { // 마감일 정렬
                query.orderBy(job.endDate.asc()); // 마감일 오름차순 정렬
            }
        }


        // 쿼리에 페이징 설정 추가
        query.offset(pageable.getOffset()).limit(pageable.getPageSize()); // 페이징 처리

        return query.fetch(); // 결과 반환
    }


    //로그인 하지 않은 경우
    @Override
    public List<Job> noLoginFindJobList(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();

    // 필터링 조건 추가
        // 지역
        if (regionList != null && !regionList.isEmpty()) {
            predicate.and(job.region.in(regionList)); // regionList에 포함된 지역에 해당하는 일자리만 필터링
        }

        // 접수 중 여부 (isRecruiting 필터링)
        if (isRecruiting != null) {
            if (isRecruiting) {
                predicate.and(job.hireType.eq(HireType.RECRUITING)); // 접수중인 일자리만 필터링
            }
        }
        JPQLQuery<Job> query = jpaQueryFactory.selectFrom(job).where(predicate);

    // 정렬 조건 추가
        if (sortBy != null) {
            if (sortBy.equals("startDate")) { // 시작일 정렬
                query.orderBy(job.startDate.asc()); // 시작일 오름차순 정렬
            } else if (sortBy.equals("endDate")) { // 마감일 정렬
                query.orderBy(job.endDate.asc()); // 마감일 오름차순 정렬
            }
        }
        query.offset(pageable.getOffset()).limit(pageable.getPageSize()); // 페이징 처리
        return query.fetch(); // 결과 반환

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
