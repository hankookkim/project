package com.example.tokenlogin.config.filter;

import com.example.tokenlogin.config.jwt.TokenProvider;
import com.example.tokenlogin.model.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
//    TokenAuthenticationFilter는 JWT 토큰을 기반으로 사용자 인증을 처리하는 필터
//    HTTP 요청에 포함된 JWT 토큰을 검사하고, 유효한 토큰이라면 사용자를 인증하여 SecurityContextHolder에 저장

    private final TokenProvider tokenProvider;  //JWT 토큰의 생성, 검증, 클레임 추출 등을 처리하는 클래스
    private final static String HEADER_AUTHORIZATION = "Authorization"; //HTTP 헤더에서 토큰을 추출할 때 사용할 키입니다. 일반적으로 Authorization 헤더를 통해 JWT 토큰을 전달
    private final static String TOKEN_PREFIX = "Bearer ";  //JWT 토큰 앞에 추가되는 접두어입니다. 예를 들어, Bearer <token> 형식으로 전달



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        OncePerRequestFilter의 추상 메서드를 오버라이드한 부분으로, 각 요청에 대해 필터링을 수행하는 핵심 로직을 담고 있습니다.

        String requestURI = request.getRequestURI();
        log.info("Request URI: {}", requestURI);
        if ("/refresh-token".equals(requestURI)) {
            filterChain.doFilter(request, response);
            return;

//            /refresh-token URI에 대해서는 인증을 건너뛰고, 요청을 그대로 필터 체인에 전달합니다.
//            일반적으로 리프레시 토큰을 처리하는 엔드포인트이므로 인증을 확인하지 않습니다.
        }



        String token= resolveToken(request); //resolveToken(request) 메서드를 통해 요청 헤더에서 JWT 토큰을 추출


        if(token != null && tokenProvider.validToken(token)==1){ //tokenProvider.validToken(token) 메서드를 통해 토큰의 유효성을 확인

            Authentication authentication= tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            getAuthentication(token)**을 사용해 인증 정보를 추출하여 **SecurityContext**에 설정

            Member member =tokenProvider.getTokenDetails(token);
            request.setAttribute("member", member);
        } else if (token !=null  && tokenProvider.validToken(token)==2){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;

//            만료된 토큰으로 간주하고, 401 Unauthorized 상태 코드를 반환
        }
        filterChain.doFilter(request, response);
//        인증 처리 후에도 필터 체인을 계속 진행합니다. filterChain.doFilter(request, response)는
//        다음 필터를 실행

    }

    private String resolveToken(HttpServletRequest request) {  //resolveToken 메서드는 HTTP 요청에서 JWT 토큰을 추출하는 역할
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        //요청 헤더에서 Authorization 값을 가져옵니다.
//        헤더는 보통 Authorization: Bearer <JWT> 형식으로 전달
        if (bearerToken == null) {
            log.warn("Authorization header is missing.");
            return null;
        }

        log.info("Authorization header received: {}", bearerToken);


        if (bearerToken !=null && bearerToken.startsWith(TOKEN_PREFIX)){
//            Authorization 헤더 값이 **Bearer **로 시작하는지 확인
//            **TOKEN_PREFIX**는 "Bearer "라는 접두어를 나타내며, JWT 토큰 앞에 붙는 표준 형식

            return bearerToken.substring(TOKEN_PREFIX.length());
//            Bearer 접두어는 제외하고 실제 JWT 토큰만 반환
//            예를 들어, Authorization: Bearer abc123xyz일 경우 abc123xyz가 반환
        }

        // Bearer 토큰이 없거나 형식이 맞지 않으면 로그를 남기고 null을 반환
        log.warn("Authorization header does not contain a valid Bearer token.");
        return null;
//        Authorization 헤더에 값이 없거나, Bearer로 시작하지 않으면 null을 반환

    }
}

//리프레시 토큰은 새로운 액세스 토큰을 발급받기 위한 용도로 사용됩니다.
///refresh-Token과 같은 엔드포인트에서 리프레시 토큰을 처리하는 경우가 많습니다.
//이를 위해 해당 URI는 인증을 건너뛰고 필터에서 바로 다음 필터로 넘어가도록 처리합니다.
//resolveToken 메서드는 HTTP 요청에서 JWT 토큰을 추출하는 중요한 역할을 합니다.
//이를 통해 유효한 토큰을 가져와 인증을 처리하는 데 사용됩니다.
//Bearer 형식에 맞는 토큰만 추출하고, 그렇지 않으면 null을 반환합니다.
//예외 처리를 통해 더 안전하게 관리할 수 있습니다.

