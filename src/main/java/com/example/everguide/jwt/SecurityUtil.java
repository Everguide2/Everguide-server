package com.example.everguide.jwt;

import com.example.everguide.api.exception.MemberBadRequestException;
import com.example.everguide.web.dto.oauth.CustomOAuth2User;
import com.example.everguide.web.dto.oauth.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public String getUserIdInSecurityContext() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId;

        if (authentication.getPrincipal().getClass().getSimpleName().equals("CustomUserDetails")) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUserId = customUserDetails.getUsername();

        } else if (authentication.getPrincipal().getClass().getSimpleName().equals("CustomOAuth2User")) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            currentUserId = customOAuth2User.getUserId();
        } else {
            throw new MemberBadRequestException("authentication.getPrincipal()의 타입이 올바르지 않음");
        }

        return currentUserId;
    }
}
