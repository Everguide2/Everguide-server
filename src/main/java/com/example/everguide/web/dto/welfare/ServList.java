package com.example.server.dto.welfare;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "servList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServList {

    @XmlElement(name = "aplyMtdNm")
    private String aplyMtdNm;

    @XmlElement(name = "bizChrDeptNm")
    private String bizChrDeptNm;

    @XmlElement(name = "ctpvNm")
    private String ctpvNm;

    @XmlElement(name = "sggNm")
    private String sggNm;

    @XmlElement(name = "servDgst")
    private String servDgst;

    @XmlElement(name = "servDtlLink")
    private String servDtlLink;

    @XmlElement(name = "lifeNmArray")
    private String lifeNmArray;

    @XmlElement(name = "intrsThemaNmArray")
    private String intrsThemaNmArray;

    @XmlElement(name = "sprtCycNm")
    private String sprtCycNm;

    @XmlElement(name = "srvPvsnNm")
    private String srvPvsnNm;

    @XmlElement(name = "inqNum")
    private int inqNum;

    @XmlElement(name = "lastModYmd")
    private String lastModYmd;

    @XmlElement(name = "servId")
    private String servId;

    @XmlElement(name = "servNm")
    private String servNm;

    @XmlElement(name = "trgterIndvdlNmArray")
    private String trgterIndvdlNmArray;
}
