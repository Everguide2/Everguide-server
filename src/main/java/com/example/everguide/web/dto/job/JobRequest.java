package com.example.everguide.web.dto.job;

import lombok.Getter;
import lombok.Setter;

public class JobRequest {

    @Getter
    @Setter
    public static class addJobBookmarkDto {
         Long memberId; //이거 삭제 예정
         Long jobId;
    }


    @Getter
    @Setter
    public static class deleteJobBookmarkDto {
        Long memberId; //이거 삭제 예정
        Long jobId;

    }
}
