package com.example.everguide.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OAuthToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;

    @JsonProperty("result")
    private String result;
}
