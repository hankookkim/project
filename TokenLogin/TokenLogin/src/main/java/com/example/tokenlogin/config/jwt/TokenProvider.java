package com.example.tokenlogin.config.jwt;

import com.example.tokenlogin.model.Member;
import com.example.tokenlogin.type.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service   //Spring Framework에서 서비스 계층을 나타내는 어노테이션. @Component의 특성을 가지며, 비즈니스 로직을 처리하는 클래스에 주로 사용.
@RequiredArgsConstructor // final 또는 @NonNull 필드를 매개변수로 가지는 생성자를 자동으로 생성. 생성자 주입을 간편하게 할 수 있는 장점.


public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date(); //현재 시간을 구하는 코드. new Date()는 현재 시간을 밀리초 단위로 반환
        return makeToken(
                member,
                new Date(now.getTime()+expiredAt.toMillis()) //now.getTime()은 현재 시간의 밀리초 값을 가져온다.
                                                            // 여기에 expiredAt.toMillis()를 더해주면, 만료 시간을 계산할 수 있음.

//                generateToken 메서드는 Member 객체와 Duration 타입의 만료 시간을 받아
//                JWT 토큰을 생성하는 메서드. 이 메서드는 현재 시간과 만료 시간을 기준으로
//                JWT 토큰을 생성하는 역할.
        );
    }

    public Member getTokenDetails(String token) {
        Claims claims = getClaims(token);     // JWT에서 클레임 추출,JWT 토큰을 파싱하여 클레임(Claims) 객체를 반환,
                                              // 클레임은 토큰에 포함된 사용자 정보나 기타 데이터를 담음

        return Member.builder()
                .id(claims.get("id", Long.class))  // 클레임에서 id 추출
                .userId(claims.getSubject())  // 클레임에서 subject (userId) 추출
                .userName(claims.get("userName", String.class))  // 클레임에서 userName 추출
                .role(Role.valueOf(claims.get("role", String.class)))  // 클레임에서 role 추출 후 Role enum으로 변환
                .build();  // Member 객체 생성

//        getTokenDetails 메서드는 JWT 토큰에서 사용자 정보를 추출하여 Member 객체를 반환하는 메서드입니다.
//        이 메서드는 JWT 토큰을 파싱하여 클레임을 추출하고, 해당 클레임을 사용해 Member 객체를 생성
//        JWT 기반 인증에서 사용자 정보를 쉽게 추출하는 데 유용

    }

    public Authentication getAuthentication(String token) {   //getAuthentication 메서드는 JWT 토큰을 기반으로
                                                            // Authentication 객체를 생성하여 반환하는 메서드입니다.
                                                            // spring Security에서 인증(Authentication) 정보를 다룰 때 사용

        Claims claims = getClaims(token);// 토큰에서 클레임을 추출

        List<GrantedAuthority> authorities= Collections.singletonList(
            new SimpleGrantedAuthority(claims.get("role",String.class))
        );
//        SimpleGrantedAuthority는 Spring Security에서 사용자의 권한을 나타내는 클래스
//        claims.get("role", String.class)에서 추출한 role 클레임 값을 사용하여 권한을 생성
//        Collections.singletonList**를 사용하여 **GrantedAuthority**를 List로 변환


        UserDetails userDetails = new User(claims.getSubject(),"",authorities);
//      UserDetails는 Spring Security에서 사용자 정보를 나타내는 인터페이스입니다.
//      claims.getSubject()는 JWT 토큰에서 사용자 ID(userId)를 가져옵니다. 이는 UserDetails에서 username에 해당
//        비밀번호는 보통 빈 문자열로 처리되며, 권한(authorities)는 이전에 생성한 리스트

        return new UsernamePasswordAuthenticationToken(userDetails,null,authorities);
//        UsernamePasswordAuthenticationToken**은 Spring Security의 인증 객체
//        userDetails는 인증된 사용자 정보를 담고, **권한(authorities)**는 해당 사용자가 가진 권한 목록
//        null은 비밀번호로 사용되며, 보통 JWT 기반 인증에서는 비밀번호가 필요하지 않기 때문에 null로 설정
    }



    public int validToken(String token) {

//        JWT 토큰의 유효성을 검사하는 메서드로, 토큰이 유효한지, 만료되었는지,
//        또는 복호화 과정에서 에러가 발생했는지를 판단


        try {
            getClaims(token);   // 토큰에서 클레임을 추출하여 유효성 검사
            return 1;// 유효한 토큰
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.info("Token이 만료되었습니다.");
            return 2;// 만료된 토큰
        } catch (Exception e) {
            // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
            System.out.println("Token 복호화 에러 : " + e.getMessage());
            return 3;// 유효하지 않은 토큰
        }
    }
    private String makeToken(Member member, Date expired) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)  // 헤더 설정
                .setIssuer(jwtProperties.getIssuer())  // 발급자 설정
                .setIssuedAt(now)  // 발급 시간 설정
                .setExpiration(expired)  // 만료 시간 설정
                .setSubject(member.getUserId())  // 사용자 ID를 subject로 설정

             //  claim은 JWT의 **페이로드(payload)**에 담을 추가적인 정보를 설정하는 메서드
                .claim("id", member.getId())  // 사용자 ID 추가 클레임
                .claim("role", member.getRole().name())  // 사용자 역할 추가 클레임
                .claim("userName", member.getUserName())  // 사용자 이름 추가 클레임
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)  // 서명 알고리즘 및 비밀 키
                .compact();  // 최종 JWT 토큰 생성,문자열로 반환


//        makeToken 메서드는 Member 객체를 바탕으로 JWT (JSON Web Token) 을 생성하는 로직을
//        포함하고 있습니다. JWT는 사용자 인증 및 권한 부여에 사용되는 토큰으로, 이 메서드는 JWT의 헤더,
//        페이로드, 서명을 설정하고 서명된 토큰을 반환


    }
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                //파서 객체는 JWT 토큰을 검증하고, JWT의 본문인 **클레임(Claims)**을 추출할 수 있는 기능을 제공

                .setSigningKey(getSecretKey())
                // JWT 서명을 검증하기 위한 비밀 키를 설정

                .build()
                //build() 메서드는 설정한 값들을 바탕으로 JWT 파서 객체를 생성

                .parseClaimsJws(token)
//                parseClaimsJws(token)는 JWT 토큰을 파싱(parse)하여 JWS(Json Web Signature) 형식의 JWT를 파싱합니다.
//                이 메서드는 JWT 토큰을 검증하고, 만약 토큰이 유효하면 클레임을 추출할 수 있게 됩니다.
//                만약 서명이나 유효 기간 등의 문제가 있으면 예외가 발생

                .getBody();
//              getBody()는 JWT의 본문(body)에 해당하는 클레임(Claims)을 반환

//        getClaims 메서드는 JWT 토큰에서 클레임(Claim) 을 추출하는 메서드입니다.
//        이 메서드는 JWT 토큰을 파싱하여 **JWT의 본문(body)**에 포함된 정보를
//        Claims 객체로 반환합니다.
    }
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);

//        getSecretKey 메서드는 JWT 토큰 서명 검증을 위한 비밀 키를 생성하는 메서드입니다.
//        이 메서드는 Base64 인코딩된 비밀 키를 디코딩하여, HMAC (Hash-based Message Authentication Code)
//        알고리즘에서 사용할 수 있는 SecretKey 객체를 반환
    }


}
