package com.example.tokenlogin.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
//        HTTP 응답에 쿠키를 추가하는 메서드입니다. 주로 사용자 인증을 위한 토큰,
//       세션 관리 등을 쿠키로 처리할 때 사용

        Cookie cookie = new Cookie(name, value);
//        Cookie 객체를 생성하고, 쿠키의 이름(name)과 값(value)을 설정합니다.
//        name은 쿠키의 이름(예: "refreshToken")이고, value는 해당 쿠키에 저장될 값(예: 토큰 값)입니다.

        cookie.setHttpOnly(true); // JavaScript에서 쿠키 접근을 막기 위한 설정
//        HttpOnly 속성은 JavaScript에서 해당 쿠키에 접근할 수 없도록 제한합니다.
//        보안을 강화하기 위한 설정으로, XSS(크로스 사이트 스크립팅) 공격을 방지할 수 있습니다.
//        예를 들어, 자바스크립트에서 document.cookie로 쿠키에 접근할 수 없게 됩니다.

        cookie.setSecure(true); // HTTPS 연결에서만 쿠키를 전송하도록 설정
//      Secure 속성은 HTTPS 연결에서만 쿠키를 전송하도록 설정합니다.
//      이를 통해 HTTP 연결에서는 쿠키가 전송되지 않도록 하여, 중간자 공격(MITM) 등으로부터 보호할 수 있습니다.

        cookie.setPath("/"); // 쿠키의 유효 경로 설정 ("/"은 전체 도메인)
//      쿠키의 유효 경로를 설정합니다. " / "는 전체 웹 애플리케이션에 대해 유효하다는 의미입니다.
//      예를 들어, "/admin"을 설정하면 /admin 경로 이하에서만 해당 쿠키를 사용할 수 있습니다.

        cookie.setMaxAge(maxAge); // 쿠키의 만료 시간 (초 단위)
//        쿠키의 만료 시간을 설정합니다. maxAge는 초 단위로 지정됩니다.
//        예를 들어, maxAge = 3600이면 쿠키는 1시간 후에 만료됩니다.
//        만약 maxAge를 0으로 설정하면 쿠키는 즉시 삭제됩니다.
//        만약 만료 시간을 지정하지 않으면, 쿠키는 세션 쿠키로 간주되어 브라우저가 종료될 때까지 유지됩니다.

        response.addCookie(cookie); // 응답에 쿠키 추가
//        response 객체의 addCookie() 메서드를 사용해 생성한 쿠키를 HTTP 응답에 추가합니다.
//        이렇게 추가된 쿠키는 클라이언트의 브라우저로 전달되고,
//        클라이언트는 이후 요청 시 해당 쿠키를 서버에 자동으로 전송합니다.

//        addCookie 메서드는 쿠키를 안전하게 설정하고 서버에서 클라이언트로 응답을 보낼 때 유용한 기능을 합니다.
//         보안과 만료 기간을 고려하여 적절히 설정하면, 쿠키를 활용한 인증이나 세션 관리에 큰 도움이 됩니다.
    }

    // 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();  // 요청에서 모든 쿠키 가져오기
//        HttpServletRequest 객체에서 제공하는 getCookies() 메서드를 사용해 요청에 포함된 쿠키 배열을 가져옵니다.
        if (cookies == null) {  // 쿠키가 없으면 반환
            return;           //아무 작업도 하지 않고 메서드를 종료
        }
// 쿠키 배열에서 하나씩 순차적으로 검사하여, 주어진 name과 일치하는 쿠키를 찾아 삭제 처리
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {  //쿠키의 이름이 메서드 인자로 전달된 name과 일치하는지 확인
                cookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정 (즉시 만료)
                cookie.setValue("");  // 쿠키 값을 빈 문자열로 설정
                cookie.setPath("/");  // 쿠키의 경로를 전체 도메인으로 설정
//                쿠키의 경로가 설정되지 않으면, 특정 경로에 한정된 쿠키가 삭제되지 않을 수 있습니다.
//                 "/"는 사이트 내 모든 경로에서 쿠키를 삭제하도록 보장
                response.addCookie(cookie); // 응답에 삭제된 쿠키 추가

//           쿠키를 삭제할 때는 **쿠키의 경로(path)**를 올바르게 설정해야 합니다.
//           쿠키가 특정 경로에만 유효한 경우, 해당 경로를 정확하게 지정하지 않으면 삭제되지 않을 수 있습니다.
//           보통 "/" 경로로 설정하여 모든 경로에서 유효하게 만듭니다.
//           Max-Age를 0으로 설정하는 방식으로 쿠키를 즉시 만료시키는 것은 쿠키 삭제의 표준적인 방법입니다.
//           로그아웃 시 Refresh Token과 같은 중요한 쿠키를 삭제하는 것은 보안상 매우 중요합니다.
//           이를 통해 사용자의 인증 정보를 안전하게 제거할 수 있습니다.


            }
        }
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {  // 쿠키가 존재하는지 확인
            for (Cookie cookie : request.getCookies()) {   // 쿠키 배열을 순차적으로 확인
                if (name.equals(cookie.getName())) {  // 쿠키의 이름이 일치하는지 확인
                    return cookie.getValue();  // 일치하면 해당 쿠키의 값을 반환
                }
            }
        }
        return null; // 일치하는 쿠키가 없으면 null 반환
    }
}