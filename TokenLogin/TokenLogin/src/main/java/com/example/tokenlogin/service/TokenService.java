package com.example.tokenlogin.service;

import com.example.tokenlogin.config.jwt.TokenProvider;
import com.example.tokenlogin.dto.SignInResponseDTO;
import com.example.tokenlogin.model.Member;
import com.example.tokenlogin.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

//    이 코드는 Refresh Token을 이용하여 새로운 Access Token과 Refresh Token을 발급하는
//    서비스를 담당하는 TokenService입니다. 해당 메서드 refreshToken은 클라이언트로부터 전달된
//    Refresh Token을 검사하고, 유효하면 새로운 Access Token과 Refresh Token을 발급하여 응답합니다.



    private final TokenProvider tokenProvider;

    // Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급하는 메서드
    public ResponseEntity<?> refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && tokenProvider.validToken(refreshToken) == 1) {
            // 1. Refresh Token이 유효하면 Member 정보 추출
//                전달된 **refreshToken**이 null이 아니고, 유효한지 검사합니다.
//                tokenProvider.validToken(refreshToken) 메서드는 해당 토큰이 유효한지 검사하고,
//                유효하면 1을 반환합니다. (검사 결과가 1이면 유효하다는 의미)


            Member member = tokenProvider.getTokenDetails(refreshToken);

//            Refresh Token이 유효한 경우, tokenProvider.getTokenDetails(refreshToken)를
//            통해 토큰에서 사용자 정보(Member 객체)를 추출합니다.
//            이 과정에서 사용자의 ID 등 중요한 정보가 포함된 Member 객체를 얻을 수 있습니다.

            // 2. 새로운 Access Token과 Refresh Token 생성
            String newAccessToken = tokenProvider.generateToken(member, Duration.ofHours(2)); // 2시간 유효
            String newRefreshToken = tokenProvider.generateToken(member, Duration.ofDays(2)); // 2일 유효

            // 3. 새로운 Refresh Token을 쿠키에 추가
            CookieUtil.addCookie(response, "refreshToken", newRefreshToken, 7 * 24 * 60 * 60); // 7일 유효

            // 4. 새로운 Access Token을 HTTP 헤더에 추가
            response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);
//            새로 생성된 Access Token을 HTTP 응답의 헤더에 Authorization 필드로 추가하여
//            클라이언트에게 전달합니다.
//            클라이언트는 이후 이 Access Token을 API 요청 헤더에 포함시켜 인증된 요청을 보낼 수 있습니다.

            // 5. 새로운 Access Token을 포함한 응답 반환
            return ResponseEntity.ok(
                    SignInResponseDTO.builder()
                            .token(newAccessToken)
                            .build()
            );
//            최종적으로, 새로운 Access Token을 포함한 **SignInResponseDTO**를 응답으로 반환합니다.
//            클라이언트는 이 응답을 통해 새로운 Access Token을 받아 사용할 수 있습니다.




        } else {
            // 6. Refresh Token이 유효하지 않으면 401 Unauthorized 응답
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh Token이 유효하지 않습니다.");

//            만약 Refresh Token이 유효하지 않다면, 401 Unauthorized 상태 코드와
//            함께 **"Refresh Token이 유효하지 않습니다."**라는 메시지를 반환합니다.
        }
    }
}

//Refresh Token을 쿠키에 추가: Refresh Token을 클라이언트가 보관할 수 있도록 쿠키에 저장하여,
//사용자가 로그아웃하거나 다른 요청을 할 때 이 쿠키를 사용할 수 있게 만듭니다.
//Access Token을 HTTP 헤더에 추가: 새로운 Access Token은 보통 클라이언트가
//API 요청을 보낼 때 Authorization 헤더에 포함시켜 전송합니다.
//refreshToken 메서드는 Refresh Token을 사용해 새로운 Access Token과 Refresh Token을 발급하는 서비스입니다.
//유효한 Refresh Token을 통해 새로운 토큰을 생성하고, 새로 생성된 토큰들을
//쿠키와 헤더에 설정하여 클라이언트에게 반환합니다.
//만약 Refresh Token이 유효하지 않다면, 401 Unauthorized 응답을 반환하여 클라이언트에게 오류를 알려줍니다.

