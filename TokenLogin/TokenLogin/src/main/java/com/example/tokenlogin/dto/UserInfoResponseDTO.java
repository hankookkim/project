package com.example.tokenlogin.dto;


import com.example.tokenlogin.type.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDTO {
    private Long id;
    private String userName;
    private String userId;
    private Role role;
}

//UserInfoResponseDTO 클래스는 사용자 정보를 담아 클라이언트에게 응답을
//반환하는 **DTO(Data Transfer Object)**입니다. 이 클래스는 사용자의
//ID, 이름, 사용자 ID, 역할 등을 포함하여 클라이언트에게 전달하는 역할을 합니다.
