package com.example.everguide.repository;

import com.example.everguide.domain.*;
import com.example.everguide.web.dto.education.EducationResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
class CustomEducationRepositoryImpl implements CustomEducationRepository {
    private final JPAQueryFactory jpaQueryFactory; //쿼리 자동생성
    private final QEducation education = QEducation.education;
    private final QBookmark bookmark = QBookmark.bookmark;

    @Override
    public Slice<Education> searchEduListByName(String name, Pageable pageable) {
        JPQLQuery<Education> query = jpaQueryFactory.selectFrom(education);

        // name이 null이 아니고 비어 있지 않으면 검색 조건 추가
        if (name != null && !name.trim().isEmpty()) {
            query.where(education.eduName.containsIgnoreCase(name)); // 이름으로 검색 (대소문자 구분 x)
        }
        query.orderBy(education.endDate.asc()); // 마감일 오름차순 정렬

        // 페이징 처리된 데이터 리스트
        List<Education> educations = query.offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1) // +1 개 가져와서 다음 페이지 여부 확인
                .fetch();

        boolean hasNext = educations.size() > pageable.getPageSize();
        if (hasNext) {
            educations.remove(educations.size() - 1);
        }

        // Slice 객체로 반환
        return new SliceImpl<>(educations, pageable, hasNext);
    }

    @Override
    public Page<Education> noLoginGetEducationList(List<String> deadlines, Pageable pageable, String keyWord) {
        BooleanBuilder predicate = new BooleanBuilder();

        // 이름 검색
        if (keyWord != null && !keyWord.trim().isEmpty()) {
            predicate.and(education.eduName.containsIgnoreCase(keyWord)); // 검색 키워드가 이름에 포함된 교육만 필터링
        }
        // 마감일 필터링
        LocalDate now = LocalDate.now();
        BooleanBuilder deadlinePredicate = new BooleanBuilder(); // 마감일 조건을 위한 별도 BooleanBuilder

        if (deadlines != null && !deadlines.isEmpty()) {
            for (String deadline : deadlines) {
                switch (deadline) {
                    case "7days":
                        deadlinePredicate.or(education.endDate.between(now, now.plusDays(7))); // 마감일이 7일 미만
                        break;
                    case "30days":
                        deadlinePredicate.or(education.endDate.between(now, now.plusDays(30))); // 마감일이 30일 미만
                        break;
                    case "over30days":
                        deadlinePredicate.or(education.endDate.after(now.plusDays(30))); // 마감일이 30일 이상
                        break;
                    case "always":
//                        deadlinePredicate.or(education.hireType.eq(HireType.ALWAYS)); // 상시 접수
                        break;
                    case "closed":
                        deadlinePredicate.or(education.endDate.before(now)); // 마감된 교육
                        break;
                    default:
                        break;
                }
            }
        }
        predicate.and(deadlinePredicate); // 마감일 조건 추가

        JPQLQuery<Education> query = jpaQueryFactory.selectFrom(education).where(predicate);

        // 정렬 조건 추가
        query.orderBy(education.endDate.asc());


        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        QueryResults<Education> results = query.fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());

    }

    @Override
    public Page<Education> getEducationList(List<String> deadlines, Pageable pageable, String keyWord, Member member) {
        BooleanBuilder predicate = new BooleanBuilder();

        // 이름 검색
        if (keyWord != null && !keyWord.trim().isEmpty()) {
            predicate.and(education.eduName.containsIgnoreCase(keyWord)); // 검색 키워드가 이름에 포함된 교육만 필터링
        }
        // 마감일 필터링
        LocalDate now = LocalDate.now();
        BooleanBuilder deadlinePredicate = new BooleanBuilder(); // 마감일 조건을 위한 별도 BooleanBuilder

        if (deadlines != null && !deadlines.isEmpty()) {
            for (String deadline : deadlines) {
                switch (deadline) {
                    case "7days":
                        deadlinePredicate.or(education.endDate.between(now, now.plusDays(7))); // 마감일이 7일 미만
                        break;
                    case "30days":
                        deadlinePredicate.or(education.endDate.between(now, now.plusDays(30))); // 마감일이 30일 미만
                        break;
                    case "over30days":
                        deadlinePredicate.or(education.endDate.after(now.plusDays(30))); // 마감일이 30일 이상
                        break;
                    case "always":
//                        deadlinePredicate.or(education.hireType.eq(HireType.ALWAYS)); // 상시 접수
                        break;
                    case "closed":
                        deadlinePredicate.or(education.endDate.before(now)); // 마감된 교육
                        break;
                    default:
                        break;
                }
            }
        }
        predicate.and(deadlinePredicate); // 마감일 조건 추가


        // 정렬 조건 추가
        // 북마크 여부 (북마크가 있으면 true, 없으면 false로 표현)
        JPQLQuery<Education> query = jpaQueryFactory.selectFrom(education)
                .leftJoin(bookmark).on(bookmark.education.eq(education).and(bookmark.member.eq(member))) // 북마크된 직업 여부 확인
                .where(predicate)
                .orderBy(
                        bookmark.id.desc().nullsLast() // 북마크 여부를 기준으로 정렬
                );


        query.orderBy(education.endDate.asc());


        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        QueryResults<Education> results = query.fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    @Override
    public EducationResponse.getEndDateCount countEducationByDeadline() {
        LocalDate now = LocalDate.now();

        // 7일 미만
        long count7Days = jpaQueryFactory
                .select(education.count())
                .from(education)
                .where(education.endDate.between(now, now.plusDays(7)))
                .fetchOne();

        // 30일 미만
        long count30Days = jpaQueryFactory
                .select(education.count())
                .from(education)
                .where(education.endDate.between(now, now.plusDays(30)))
                .fetchOne();

        // 30일 이상
        long countOver30Days = jpaQueryFactory
                .select(education.count())
                .from(education)
                .where(education.endDate.after(now.plusDays(30)))
                .fetchOne();

//        // 상시 접수 (조건에 따라 추가)
        long countAlways = jpaQueryFactory
                .select(education.count())
                .from(education)
                .where(education.endDate.isNull())  // 상시 접수 조건을 추가해야 합니다.
                .fetchOne();

        // 마감된 교육
        long countClosed = jpaQueryFactory
                .select(education.count())
                .from(education)
                .where(education.endDate.before(now))
                .fetchOne();

        // DTO 빌더 사용해 반환
        return EducationResponse.getEndDateCount.builder()
                ._7days(String.valueOf(count7Days))
                ._30days(String.valueOf(count30Days))
                .over30days(String.valueOf(countOver30Days))
                .always(String.valueOf(countAlways))
                .closed(String.valueOf(countClosed))
                .build();
    }
    }


