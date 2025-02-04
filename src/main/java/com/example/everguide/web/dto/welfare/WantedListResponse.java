package com.example.everguide.web.dto.welfare;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "wantedList")
@XmlAccessorType(XmlAccessType.FIELD)
public class WantedListResponse {
    
    @XmlElement(name = "totalCount")
    private int totalCount;

    @XmlElement(name = "pageNo")
    private int pageNo;

    @XmlElement(name = "numOfRows")
    private int numOfRows;

    @XmlElement(name = "resultCode")
    private String resultCode;

    @XmlElement(name = "resultMessage")
    private String resultMessage;

    @XmlElement(name = "servList")
    private List<ServList> servList;
}
