package com.example.everguide.web.dto.oauth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {

    private String role;
    private String name;
    private String userId;
    private String password;
}
