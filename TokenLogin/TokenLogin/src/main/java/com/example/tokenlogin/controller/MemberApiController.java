package com.example.tokenlogin.controller;


import com.example.tokenlogin.config.jwt.TokenProvider;
import com.example.tokenlogin.config.security.CustomUserDetails;
import com.example.tokenlogin.dto.*;
import com.example.tokenlogin.model.Member;
import com.example.tokenlogin.service.MemberService;
import com.example.tokenlogin.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/join") //회원 가입을 처리하는 API
    public SIgnUpResponseDTO signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        System.out.println(signUpRequestDTO);
        System.out.println("Received role:" + signUpRequestDTO.getRole());
//        요청으로 받은 signUpRequestDTO 객체와 그 안에 포함된 role 값을 로그로 출력하여 디버깅에 사용

        memberService.signUp(signUpRequestDTO.toMember(bCryptPasswordEncoder));
//        회원 가입 로직을 처리하여, 새로운 사용자를 데이터베이스에 저장하는 작업 수행
//        SignUpRequestDTO 객체에 포함된 정보를 Member 엔티티로 변환하는 메서드
//        비밀번호는 bCryptPasswordEncoder를 사용해 암호화해서 저장

        return SIgnUpResponseDTO.builder()
                .successed(true)
                .build();
//        회원 가입이 성공적으로 처리된 후, SIgnUpResponseDTO 객체를 반환
//        successed 값이 true로 설정되어 회원 가입 성공을 나타냄


//        매개변수:
//        @RequestBody SignUpRequestDTO signUpRequestDTO: 요청 본문에서 전달된 회원 가입 정보 (예: 아이디, 비밀번호, 역할 등)
//        반환 타입:
//        SIgnUpResponseDTO: 회원 가입 처리 결과를 담은 응답 DTO


    }

    @PostMapping("/login") //로그인 요청을 처리하는 API,
    // POST /login 요청을 받아 사용자의 로그인 인증을 처리하고, JWT 토큰을 발급하여 응답
    public SignInResponseDTO signIn(@RequestBody SignInRequestDTO signInRequestDTO,
                                    HttpServletResponse response) {
//        매개변수:
//        @RequestBody SignInRequestDTO signInRequestDTO: 로그인 요청 정보 (아이디, 비밀번호 등)
//        HttpServletResponse response: 응답에 refreshToken을 담을 HttpServletResponse 객체




        System.out.println(signInRequestDTO);

        Authentication authenticate = authenticationManager.authenticate(
                //authenticationManager.authenticate(...)를 사용하여 사용자 "인증"을 처리
                new UsernamePasswordAuthenticationToken(
                        signInRequestDTO.getUserName(),
                        signInRequestDTO.getPassword()

//              UsernamePasswordAuthenticationToken을 생성하여, 전달된 사용자 이름과 비밀번호를
//              authenticationManager에 전달하고 인증을 시도
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authenticate);
//        인증된 결과를 SecurityContextHolder에 저장하여 이후의 요청에서 인증 정보를 사용할 수 있게 설정

        Member member =((CustomUserDetails)authenticate.getPrincipal()).getMember();
//        authenticate.getPrincipal()을 통해 인증된 사용자 정보를 가져오고,
//        이를 CustomUserDetails로 캐스팅하여 Member 객체를 얻음

        String accessToken= tokenProvider.generateToken(member, Duration.ofHours(2));
        String refreshToken = tokenProvider.generateToken(member, Duration.ofDays(2));
//        tokenProvider.generateToken(...)를 사용하여 **accessToken**과 **refreshToken**을 생성
//        accessToken: 로그인 후 짧은 유효 기간 (2시간) 동안 유효한 토큰
//        refreshToken: 긴 유효 기간 (2일) 동안 유효한 토큰


        CookieUtil.addCookie(response,"refreshToken",refreshToken,7*24*60*60);
//        CookieUtil.addCookie(...) 메서드를 사용하여 **refreshToken**을 응답 쿠키에 저장
//        쿠키의 유효 기간은 7일 (7 * 24 * 60 * 60초)

        return SignInResponseDTO.builder()
                .isLoggined(true)
                .token(accessToken)
                .userId(member.getUserId())
                .userName(member.getUserName())
                .build();

//        SignInResponseDTO 객체를 생성하여 로그인 성공 결과를 응답
//        응답에는 accessToken, userId, userName 등의 정보가 포함됨

//        로그인 처리 API로 사용자가 보내는 사용자명과 비밀번호를 통해 인증을 수행
//        JWT 토큰을 발급하여 응답 본문에 accessToken을 포함하고, refreshToken은 쿠키에 저장
//        회원 정보는 CustomUserDetails에서 가져오며, 이를 통해 JWT 토큰 생성에 필요한 정보를 활용


    }
    @PostMapping("/logout") //로그아웃 요청을 처리하는 API
//    로그아웃 처리의 핵심은 refreshToken 쿠키를 삭제하는 것

    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        요청에 HttpServletRequest와 HttpServletResponse 객체가 포함되어 있어,
//        이 객체들을 사용하여 쿠키 삭제 작업을 수행

        CookieUtil.deleteCookie(request, response, "refreshToken");
//        CookieUtil.deleteCookie() 메서드를 호출하여, 클라이언트가 보낸 요청에서
//        refreshToken 쿠키를 삭제합니다.
//        로그아웃 시 **refreshToken**을 쿠키에서 삭제함으로써, 사용자는 더 이상 해당
//        refreshToken을 사용하여 새로운 accessToken을 발급받을 수 없게 됩니다.
//        deleteCookie 메서드는 일반적으로 다음과 같은 작업을 수행할 수 있습니다:
//        쿠키의 이름을 **refreshToken**으로 지정
//        쿠키의 만료 시간을 과거 시간으로 설정하여 쿠키를 삭제 처리
//        해당 쿠키가 **어떤 경로(path)**와 **도메인(domain)**에 설정되었는지에 따라 쿠키를 삭제
//        클라이언트의 refreshToken 쿠키가 삭제되고, 사용자는 로그아웃된 상태로 더 이상 인증된 요청을 할 수 없습니다.

    }

    @GetMapping("/user/info")
    public UserInfoResponseDTO getUserInfo(HttpServletRequest request) {

          Member member = (Member) request.getAttribute("member");
//        이 부분은 member 객체를 요청에 첨부하여 인증된 사용자 정보를 전달받는 부분입니다.
//        Spring Security나 필터를 사용해 인증된 사용자의 정보를 HttpServletRequest
//        객체에 설정할 수 있습니다. 예를 들어, 로그인 성공 후 Authentication 객체에서 사용자
//        정보를 추출하여 request.setAttribute("member", member)로 설정할 수 있습니다.
//        요청을 받으면 HttpServletRequest 객체를 통해 요청 헤더 또는 세션에 저장된 사용자 정보를 가져옵니다.
//        여기서 request.getAttribute("member")는 인증된 사용자의 Member 객체를 가져옵니다.
//        이 Member 객체는 로그인 과정에서 Authentication 객체를 통해 설정되었을 가능성이 큽니다.

        return UserInfoResponseDTO.builder()
                .id(member.getId())
                .userName(member.getUserName())
                .userId(member.getUserId())
                .role(member.getRole())
                .build();

//        Member 객체에서 사용자의 ID, 사용자명, 아이디, 역할 등을 추출한 후,
//        이를 UserInfoResponseDTO 객체에 담아서 응답합니다.
//        이 DTO는 클라이언트가 사용자의 기본 정보를 쉽게 확인할 수 있도록 구조화된 데이터를 제공합니다.
    }

}

