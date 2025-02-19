package com.example.everguide.web.dto.education;


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
public class EducationResponseBody {

    @XmlElement(name = "totalCount")
    private int totalCount; // 총 데이터 개수

    @XmlElement(name = "pageNo")
    private int pageNo; // 현재 페이지 번호

    @XmlElement(name = "numOfRows")
    private int numOfRows; // 한 페이지 당 데이터 개수

    @XmlElementWrapper(name = "items")
    @XmlElement(name = "item")
    private List<EducationItem> educationList; // 개별 아이템 리스트
}
