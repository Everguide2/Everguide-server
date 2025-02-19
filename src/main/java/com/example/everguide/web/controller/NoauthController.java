package com.example.everguide.web.controller;

import com.example.everguide.api.ApiResponse;
import com.example.everguide.api.code.status.ErrorStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NoauthController {

    // OAuth2 기본 로그인 창 띄우지 않기 위함
    @Operation(hidden = true)
    @GetMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuthGet() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }

    @Operation(hidden = true)
    @PostMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuthPost() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }

    @Operation(hidden = true)
    @PutMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuthPut() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }

    @Operation(hidden = true)
    @PatchMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuthPatch() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }

    @Operation(hidden = true)
    @DeleteMapping("/noauth")
    public ResponseEntity<ApiResponse<Map<String, String>>> noAuthDelete() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED, "unauthorized"));
    }
}
