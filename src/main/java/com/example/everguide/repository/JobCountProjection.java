package com.example.everguide.repository;

public interface JobCountProjection {
    String getRegion();  // 지역 필드
    Long getCount();     // 일자리 수
}