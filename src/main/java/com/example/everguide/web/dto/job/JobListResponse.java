package com.example.everguide.web.dto.job;

import com.example.everguide.domain.Education;
import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobListResponse {

    @XmlElement(name = "header")
    private JobResponseHeader header; // 헤더 정보

    @XmlElement(name = "body")
    private JobResponseBody body; // 바디 정보
}