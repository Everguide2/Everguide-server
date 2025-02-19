package com.example.everguide.repository;

import com.example.everguide.domain.Education;
import com.example.everguide.domain.Job;
import com.example.everguide.domain.QEducation;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
class CustomEducationRepositoryImpl implements CustomEducationRepository {
    private final JPAQueryFactory jpaQueryFactory; //쿼리 자동생성
    private final QEducation education = QEducation.education;

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
}
