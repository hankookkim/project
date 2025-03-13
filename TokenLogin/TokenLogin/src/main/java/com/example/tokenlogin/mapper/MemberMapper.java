package com.example.tokenlogin.mapper;


import com.example.tokenlogin.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void saved(Member member);

    Member findByUserId(String userId);


}
