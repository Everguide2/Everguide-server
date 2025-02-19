package com.example.everguide.web.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {

    private String role;
    private String name;
    private String userId;
    private String social;
}
