package com.example.everguide.web.dto.job;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class JobResponseHeader {

    @XmlElement(name = "resultCode")
    private String resultCode; // 응답 코드

    @XmlElement(name = "resultMsg")
    private String resultMessage; // 응답 메시지
}
