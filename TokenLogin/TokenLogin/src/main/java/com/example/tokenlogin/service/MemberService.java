package com.example.tokenlogin.service;


import com.example.tokenlogin.dto.UserInfoResponseDTO;
import com.example.tokenlogin.mapper.MemberMapper;
import com.example.tokenlogin.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService { //MemberService 클래스는 사용자 가입(회원가입) 처리를 담당하는 서비스 클래스

    private final MemberMapper memberMapper;

    public void signUp(Member member) {
        memberMapper.saved(member);
    }

//    public UserInfoResponseDTO getUserInfo(Long id) {
//        Member member = memberMapper.findById(id);
//        return UserInfoResponseDTO.builder()
//                .Id(member.getId())
//                .userName(member.getUserName())
//                .userId(member.getUserId())
//                .role(member.getRole())
//                .build();
//    }
}
