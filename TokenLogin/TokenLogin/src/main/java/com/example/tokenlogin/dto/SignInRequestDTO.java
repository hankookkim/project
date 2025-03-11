package com.example.tokenlogin.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignInRequestDTO {
//    SignInRequestDTO 클래스는 사용자가 로그인할 때 필요한 정보를
//    담는 DTO(Data Transfer Object)입니다. 주로 클라이언트가 로그인 요청을 서버로 보낼 때 사용

    private String userName;

    private String password;
}
