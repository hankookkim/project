package com.example.tokenlogin.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SIgnUpResponseDTO {
//    ignUpResponseDTO 클래스는 회원 가입 응답을 나타내는 **DTO
//    (Data Transfer Object)**입니다. 이 클래스는 클라이언트에게 회원 가입 결과를 전달할 때 사용

    private boolean success;   // 회원 가입 성공 여부를 나타내는 필드
//    성공 시 true, 실패 시 false 값이 담깁니다
}

//SignUpResponseDTO는 success 필드만 포함하는 간단한 DTO입니다. 이 DTO를 사용하여 클라이언트에게 성공 여부를 전달합니다.
//@Builder를 사용하여 객체 생성 시 코드가 더 깔끔하고 직관적으로 작성됩니다.
//회원 가입 처리 후, 성공 여부를 SignUpResponseDTO로 반환하여 클라이언트가 성공/실패를 확인할 수 있도록 합니다.
//이렇게 DTO를 사용하는 방식은 응답 데이터를 구조화하고, 클라이언트와의 데이터 전송을 명확하게 해주는 좋은 방법입니다.