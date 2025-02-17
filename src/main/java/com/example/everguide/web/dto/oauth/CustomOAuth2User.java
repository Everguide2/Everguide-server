package com.example.everguide.web.dto.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final SocialMemberDTO socialMemberDTO;

    public CustomOAuth2User(SocialMemberDTO socialMemberDTO) {
        this.socialMemberDTO = socialMemberDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return socialMemberDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return socialMemberDTO.getName();
    }

    public String getUserId() {

        return socialMemberDTO.getUserId();
    }

    public String getSocial() {

        return socialMemberDTO.getSocial();
    }
}
