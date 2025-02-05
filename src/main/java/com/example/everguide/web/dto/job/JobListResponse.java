package com.example.everguide.web.dto.job;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobListResponse {

    @XmlElement(name = "resultCode")
    private String resultCode; // 응답 코드

    @XmlElement(name = "resultMsg")
    private String resultMessage; // 응답 메시지

    @XmlElement(name = "totalCount")
    private int totalCount; // 총 데이터 개수

    @XmlElement(name = "pageNo")
    private int pageNo; // 현재 페이지 번호

    @XmlElement(name = "numOfRows")
    private int numOfRows; // 한 페이지 당 데이터 개수

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<JobItem> jobList; // 개별 아이템 리스트
}
