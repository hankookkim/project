package com.example.tokenlogin.dto;

import com.example.tokenlogin.model.Member;
import com.example.tokenlogin.type.Role;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@ToString
public class SignUpRequestDTO {
    private String userId;
    private String password;
    private String userName;
    private Role role;

    public Member toMember(BCryptPasswordEncoder passwordEncoder) { //회원가입 요청 데이터를 Member 엔티티로 변환하는 역할
        return Member.builder()
                .userId(userId)
                .password(passwordEncoder.encode(password)) // 비밀번호 암호화
                .userName(userName)
                .role(role)
                .build();
//      passwordEncoder.encode(password) 부분에서 BCrypt 암호화를 적용하여 비밀번호를 안전하게 저장합니다.
//      Spring Security에서 제공하는 BCryptPasswordEncoder를 사용하면, 비밀번호를 해시(hash)화하여 저장할 수 있습니다.
//      이렇게 하면 DB에 암호화된 비밀번호가 저장되므로 보안성이 높아집니다.

    }
}
