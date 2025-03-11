package com.example.tokenlogin.service;


import com.example.tokenlogin.config.security.CustomUserDetails;
import com.example.tokenlogin.mapper.MemberMapper;
import com.example.tokenlogin.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
//    이 클래스는 Spring Security의 UserDetailsService 인터페이스를 구현하여 사용자 인증을 처리합니다.
//    사용자 정보를 데이터베이스에서 가져오고, 이를 UserDetails 객체로 반환하는 역할

 private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Spring Security에서 사용자 인증 시 호출되는 메서드입니다.


        Member member=memberMapper.findByUserId(username);  // 데이터베이스에서 사용자 정보를 조회
        //        username(사용자 아이디)을 입력받아, memberMapper.findByUserId(username)로
        //        해당 사용자를 데이터베이스에서 조회

        if (member==null) { // 사용자 정보가 없으면 예외 발생
            throw new UsernameNotFoundException(username+" not found");
//            만약 데이터베이스에서 해당 사용자를 찾을 수 없다면, UsernameNotFoundException을 던집니다.
//            이 예외는 Spring Security가 처리하여 로그인 실패 처리를 합니다.

        }
        return CustomUserDetails.builder()  // CustomUserDetails 객체를 생성하여 사용자 정보(Member)와 역할(role)을 반환
                .member(member)
                .roles(List.of(String.valueOf(member.getRole().name())))
//                List.of(String.valueOf(member.getRole().name()))를 통해 역할을 리스트 형식으로 변환하여 전달합니다.
//                이 리스트는 GrantedAuthority로 처리되어 인증 및 권한 부여에 사용됩니다.
                .build();
    }
}

//UserDetailService는 Spring Security의 사용자 인증 시스템에서 핵심적인 역할을 합니다.
//loadUserByUsername 메서드는 사용자가 입력한 아이디로 사용자 정보를 데이터베이스에서 조회하고,
//해당 정보를 CustomUserDetails 객체로 반환하여 인증 및 권한 부여에 사용됩니다.
//이 구현은 기본적인 인증 처리에 적합하며, 보안 요구사항에 맞게 확장할 수 있습니다.
