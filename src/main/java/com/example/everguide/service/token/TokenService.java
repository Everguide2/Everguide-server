package com.example.everguide.service.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TokenService {

    Boolean cookieToHeader(HttpServletRequest request, HttpServletResponse response);

    boolean reissue(HttpServletRequest request, HttpServletResponse response);
}
