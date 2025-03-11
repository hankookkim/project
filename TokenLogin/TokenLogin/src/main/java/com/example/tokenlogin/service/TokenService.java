package com.example.tokenlogin.service;

import com.example.tokenlogin.config.jwt.TokenProvider;
import com.example.tokenlogin.dto.SignInResponseDTO;
import com.example.tokenlogin.model.Member;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;

    // Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급하는 메서드
    public ResponseEntity<?> refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && tokenProvider.validToken(refreshToken) == 1) {
            // 1. Refresh Token이 유효하면 Member 정보 추출
            Member member = tokenProvider.getTokenDetails(refreshToken);

            // 2. 새로운 Access Token과 Refresh Token 생성
            String newAccessToken = tokenProvider.generateToken(member, Duration.ofHours(2)); // 2시간 유효
            String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(2)); // 2일 유효

            // 3. 새로운 Refresh Token을 쿠키에 추가
            CookieUtil.addCookie(response, "refreshToken", newRefreshToken, 7 * 24 * 60 * 60); // 7일 유효

            // 4. 새로운 Access Token을 HTTP 헤더에 추가
            response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);

            // 5. 새로운 Access Token을 포함한 응답 반환
            return ResponseEntity.ok(
                    SignInResponseDTO.builder()
                            .token(newAccessToken)
                            .build()
            );
        } else {
            // 6. Refresh Token이 유효하지 않으면 401 Unauthorized 응답
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh Token이 유효하지 않습니다.");
        }
    }
}
