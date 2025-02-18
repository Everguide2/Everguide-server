package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public ApiResponse<String> test() {
        return ApiResponse.onSuccess(SuccessStatus._OK, "Hello, World");
    }
} 