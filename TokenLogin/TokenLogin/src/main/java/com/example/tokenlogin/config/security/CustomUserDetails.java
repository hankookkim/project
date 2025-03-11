package com.example.tokenlogin.config.security;


import com.example.tokenlogin.model.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
//    CustomUserDetails 클래스는 Spring Security에서 UserDetails 인터페이스를 구현한
//    사용자 정보 클래스로, 사용자에 대한 인증 및 권한 부여를 담당합니다. 이 클래스는 Spring Security의
//      **인증(Authentication)**과 권한 부여(Authorization) 과정에서 사용

    private Member member;  //Member 객체를 포함하여 회원의 상세 정보(아이디, 비밀번호 등)를 관리

    private List<String> roles;  //사용자의 역할(role) 목록을 나타냅니다. roles는 사용자에게 할당된 권한을 저장


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //UserDetails의 메서드로 사용자의 권한을 반환
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
//        roles 리스트를 SimpleGrantedAuthority로 변환하여 GrantedAuthority 컬렉션을 반환합니다.
//        이 컬렉션은 Spring Security가 권한을 확인하는 데 사용
    }

    @Override
    public String getPassword() {
//        UserDetails의 메서드로 사용자의 비밀번호를 반환
//        이 메서드는 로그인 시 사용자 비밀번호를 Spring Security에 제공하는 역할
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
//        UserDetails의 메서드로 사용자의 아이디(로그인 시 사용하는 사용자명을 반환)
//        getUsername()은 사용자 식별자로 아이디를 사용하며, CustomUserDetails 클래스에서는 member.getUserId()
    }

    @Override
    public boolean isAccountNonExpired() {
//        UserDetails의 메서드로 계정이 만료되었는지 여부를 반환합니다.
//        항상 true를 반환하여 계정이 만료되지 않도록 설정
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
//        UserDetails의 메서드로 계정이 잠겨있는지 여부를 반환
//        항상 true를 반환하여 계정이 잠기지 않도록 설정
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
//        UserDetails의 메서드로 비밀번호가 만료되었는지 여부를 반환
//        항상 true를 반환하여 비밀번호가 만료되지 않도록 설정합니다.
        return true;
    }

    @Override
    public boolean isEnabled() {
//        UserDetails의 메서드로 계정이 활성화되었는지 여부를 반환
//        항상 true를 반환하여 계정이 활성화되도록 설정

        return true;
    }
}


//CustomUserDetails는 Spring Security의 **인증(Authentication)**을 위한 중요한 클래스입니다.
//사용자 정보와 그에 관련된 권한을 다루며, UserDetails 인터페이스를 구현하여 Spring Security가 해당
//사용자에 대한 정보를 사용할 수 있도록 합니다. 시스템의 인증 및 권한 부여가 적절하게 이루어지기 위해,
//필요한 메서드를 정확하게 구현하고, 보안 요구사항에 맞게 수정 및 확장할 수 있습니다.
