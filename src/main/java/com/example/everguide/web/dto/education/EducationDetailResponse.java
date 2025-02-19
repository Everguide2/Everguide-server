package com.example.everguide.web.dto.education;


import com.example.everguide.web.dto.job.JobResponseHeader;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class EducationDetailResponse {

    @XmlElement(name = "header")
    private JobResponseHeader header; // 헤더 정보


    @XmlElement(name = "body")
    private EducationDetailResponseBody body;
}
