package com.example.everguide.web.dto.education;


import com.example.everguide.web.dto.job.JobItemDetail;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class EducationDetailResponseBody {
    @XmlElement(name = "totalCount")
    private int totalCount; // 총 데이터 개수

    @XmlElement(name = "pageNo")
    private int pageNo; // 현재 페이지 번호

    @XmlElement(name = "numOfRows")
    private int numOfRows; // 한 페이지 당 데이터 개수


    @XmlElementWrapper(name = "items")  // "items" 태그를 감싸는 래퍼
    @XmlElement(name = "item")
    private List<EducationItemDetail> jobDetail; //일자리 상세 정보
    //일자리 상세 정보
}
