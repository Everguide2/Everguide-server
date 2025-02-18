package com.example.everguide.service.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomTokenResponseConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {


    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {

        String accessToken = (String) tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
        String refreshToken = (String) tokenResponseParameters.get(OAuth2ParameterNames.REFRESH_TOKEN);
        long expiresIn;
        if (tokenResponseParameters.containsKey("refresh_token_expires_in")) { // 카카오
            Object expiresInObj = tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
            expiresIn = ((Number) expiresInObj).longValue();

        } else { // 네이버
            String expiresInStr = (String) tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN);
            expiresIn = Long.parseLong(expiresInStr);
        }

        Set<String> scopes = Collections.emptySet();
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
            String scope = (String) tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " "))
                    .collect(Collectors.toSet());
        }

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(expiresIn)
                .scopes(scopes)
                .refreshToken(refreshToken)
                .additionalParameters(Map.of(OAuth2ParameterNames.REFRESH_TOKEN, refreshToken))
                .build();
    }
}
