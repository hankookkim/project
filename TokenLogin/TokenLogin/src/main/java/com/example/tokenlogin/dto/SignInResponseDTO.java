package com.example.tokenlogin.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponseDTO {

//    SignInResponseDTO 클래스는 사용자가 로그인한 후 서버가 클라이언트에
//    반환할 응답 데이터를 담는 DTO(Data Transfer Object)입니다.
//    주로 로그인 성공 후, 클라이언트에게 전달할 정보를 포함합니다.

    private boolean isLoggined;  // 로그인 여부 (로그인 성공 또는 실패를 나타냄)
//    isLoggined: 사용자가 로그인한 상태인지를 나타내는 필드입니다. 로그인 성공 시 "true"나 "success"
//    등을 반환하고, 실패 시 "false"나 "fail" 등을 반환할 수 있습니다.
    private String userId;
    private String userName;
    private String token;   // 로그인 성공 시 발급되는 JWT 토큰
//    token: 로그인 성공 시 발급되는 JWT 토큰입니다. 이 토큰은 사용자가 인증된 상태에서
//    API를 호출할 때 필요하며, 서버가 요청을 처리할 때 인증을 확인하는 데 사용
}
//SignInResponseDTO는 로그인 처리 후, 클라이언트에게 반환할 정보를 포함하는 DTO입니다.
//로그인 성공 시 사용자 정보와 JWT 토큰을 클라이언트에 반환하며, 실패 시 로그인 실패 상태만 반환합니다.
//@Builder와 @Getter 어노테이션을 사용해 코드가 간결하고, 객체를 쉽게 생성할 수 있도록 돕습니다.