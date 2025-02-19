package com.example.everguide.web.dto.education;


import lombok.Getter;
import lombok.Setter;


public class EducationRequest {

    @Getter
    @Setter
    public static class addEduBookmarkDto {
        Long educationId;
    }


    @Getter
    @Setter
    public static class deleteEduBookmarkDto {
        Long educationId;

    }
}
