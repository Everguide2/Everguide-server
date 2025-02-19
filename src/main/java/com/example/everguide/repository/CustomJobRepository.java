package com.example.everguide.repository;

import com.example.everguide.domain.Job;
import com.example.everguide.domain.Member;
import com.example.everguide.domain.enums.Region;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CustomJobRepository {
    List<Job> findJobList(List<Region> regionList, String sortBy, Boolean isRecruiting, Pageable pageable, Member member);
    List<Job> noLoginFindJobList(List<Region> regionList, String sortBy, Boolean isRecruiting,  Pageable pageable);


    List<Job> findThisWeekJobList();
    Slice<Job> searchJobListByName(String name, Pageable pageable);
}
