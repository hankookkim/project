package com.example.tokenlogin.controller;


import com.example.tokenlogin.service.TokenService;
import com.example.tokenlogin.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenApiController {



    private final TokenService tokenService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 클라이언트로부터 Refresh Token을 가져옵니다.
        String refreshToken = CookieUtil.getCookieValue(request, "refreshToken");

//        클라이언트가 쿠키에 저장한 Refresh Token을 가져옵니다.
//        CookieUtil.getCookieValue(request, "refreshToken") 메서드는
//        요청에서 "refreshToken"이라는 이름의 쿠키 값을 추출합니다.
//        이를 통해 클라이언트가 저장한 Refresh Token을 읽어옵니다.

        // 2. TokenService의 refreshToken 메서드를 호출하여 새로운 토큰을 처리합니다.
        return tokenService.refreshToken(refreshToken, response);

//        tokenService.refreshToken(refreshToken, response)는 Refresh Token을 사용하여 새로운
//        Access Token과 Refresh Token을 생성하는 메서드입니다.
//        이 메서드는 TokenService에서 실행되며, 새로운 토큰을 생성한 후 이를 **응답(response)**에
//        설정하여 클라이언트에 전달합니다.
    }
}

//클라이언트 요청: 클라이언트가 /refresh-token API를 호출하면, Refresh Token을 요청
//헤더나 쿠키에 포함시켜 전송합니다.
//서버 처리: 서버는 쿠키에서 Refresh Token을 추출하고, 이를 TokenService에 전달하여
//새로운 Access Token을 생성합니다.
//응답: 생성된 Access Token과 새로운 Refresh Token을 클라이언트에게 반환합니다.