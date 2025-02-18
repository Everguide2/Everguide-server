package com.example.everguide.web.dto.job;

import lombok.Getter;
import lombok.Setter;

public class JobRequest {

    @Getter
    @Setter
    public static class addJobBookmarkDto {
         Long jobId;
    }


    @Getter
    @Setter
    public static class deleteJobBookmarkDto {
        Long jobId;

    }
}
