package com.example.everguide.oauth2;

import com.example.everguide.jwt.JWTUtil;
import com.example.everguide.redis.RedisUtils;
import com.example.everguide.web.dto.auth.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RedisUtils redisUtils;

    public CustomSuccessHandler(JWTUtil jwtUtil, RedisUtils redisUtils) {

        this.jwtUtil = jwtUtil;
        this.redisUtils = redisUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String userId = customUserDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String social = customUserDetails.getSocial();

        String refresh = jwtUtil.createJwt(userId, role, social, "refresh", 60000*60*24L);

        redisUtils.setLocalRefreshToken(userId, refresh, 60000*60*24L);

        response.addCookie(createCookie("refresh", refresh));
        response.sendRedirect("http://localhost:5173/cookie-to-header");
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*10);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
