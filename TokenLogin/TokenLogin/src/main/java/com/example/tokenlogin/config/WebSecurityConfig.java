package com.example.tokenlogin.config;


import com.example.tokenlogin.config.filter.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration //설정 클래스를 정의할 때 사용하는 어노테이션,하나 이상의 @Bean 메서드를 포함,Spring 컨테이너가 해당 클래스를 설정 파일로 인식하고 관리
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled=true) //메서드 수준의 보안을 활성화하는 어노테이션.
public class WebSecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){  //특정 경로에 대해 보안 설정 무시
        return (web)->web.ignoring()  //web.ignoring() 메서드는 지정된 URL 패턴에 대해서 보안 필터를 적용하지 않도록 설정
                .requestMatchers( //*requestMatchers(...)**는 보안 설정을 무시할 URL 패턴을 지정
                        "/static/**",    //입력된 경로 하위의 모든 파일들
                        "/css/**",
                        "/js/**"
                );
    }
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http)throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)// CSRF 보호 비활성화
                .sessionManagement(
                        session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                   세션을 사용하지 않고, JWT와 같은 상태 없는 인증 방식을 사용합니다. 이로 인해 서버는
//                   세션을 유지하지 않고, 클라이언트가 보낸 토큰으로 인증을 처리합니다.
                .authorizeHttpRequests(
                        auth->auth
                                //   .requestMatchers("/api/board/**").hasRole("ADMIN")
                                .requestMatchers(

                                        new AntPathRequestMatcher("/", GET.name()),
                                        new AntPathRequestMatcher("/member/join", GET.name()),
                                        new AntPathRequestMatcher("/member/login", GET.name()),
                                        new AntPathRequestMatcher("/write", GET.name()),
                                        new AntPathRequestMatcher("/detail", GET.name()),
                                        new AntPathRequestMatcher("/access-denied", GET.name()),
                                        new AntPathRequestMatcher("/update", GET.name()),
                                        new AntPathRequestMatcher("/refresh-token", POST.name()),
                                        new AntPathRequestMatcher("/join", POST.name()),
                                        new AntPathRequestMatcher("/login", POST.name()),
                                        new AntPathRequestMatcher("/logout", POST.name()),
                                        new AntPathRequestMatcher("/api/board/file/download/*", GET.name())
                                ).permitAll()   // 위의 경로는 인증 없이 접근 가능
                                .anyRequest().authenticated() // 그 외의 요청은 인증 필요
                )
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 설정 비활성화
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//        tokenAuthenticationFilter는 JWT 토큰을 검증하는 필터입니다. 이 필터는 UsernamePasswordAuthenticationFilter
//        앞에 추가되며, JWT 기반 인증을 처리합니다.

                .exceptionHandling(exception->exception
                        .authenticationEntryPoint(authenticationEntryPoint()) // 인증되지 않은 요청에 대한 예외를 처리
                        .accessDeniedHandler(accessDeniedHandler()) // 사용자가 권한이 없을때 발생하는 예외를 처리
                );
        return http.build(); // 설정된 HttpSecurity 객체 반환

//        HTTP 요청에 대한 보안규칙을 정의, 특정경로에 대해서만 접근을 허용,그외의 요청은 인증을 요구하는 방식.
//        jwt토큰 인증을 위한 필터와 예외처리를 설정.


    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

//    AuthenticationConfiguration 객체를 주입받아
//    getAuthenticationManager()를 호출하여 AuthenticationManager를 반환합니다.
//    AuthenticationManager는 인증을 처리하는 핵심 컴포넌트입니다.

    @Bean
    public BCryptPasswordEncoder bCryptpasswordEncoder(){
        return new BCryptPasswordEncoder();

//        BCryptPasswordEncoder는 단방향 해싱 방식을 사용하여 비밀번호를 안전하게 저장하는 데 사용됩니다.
//        Spring Security에서는 사용자 비밀번호를 평문(plain text)으로 저장하는 것을 금지하고, 반드시 암호화해야 합니다.

    }
    @Bean
    AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException)->{
            response.sendRedirect("/access-denied");

//             로그인했지만 권한이 부족한 사용자가 접근할 때 실행
//            사용자가 인가되지 않은(권한이 없는) 페이지에 접근할 경우 실행
//            Spring Security에서 인가(Authorization) 실패 시 기본적으로
//            403(Forbidden) 오류 페이지가 표시됩니다.
//            하지만 사용자가 더 나은 사용자 경험을 제공하기 위해
//            403 오류 대신 특정 페이지로 이동하도록 설정할 수 있습니다.
        };
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return (request, response, accessDeniedException)->{
            response.sendRedirect("/access-denied");

//            비회원(로그인하지 않은 사용자)이 보호된 리소스에 접근할 때 실행
//            인증되지 않은 사용자가 보호된 페이지에 접근할 경우 실행됨.
//            기본적으로 Spring Security는 인증되지 않은 사용자가 보호된 리소스에 접근하면
//            로그인 페이지로 리디렉트하거나 401(Unauthorized) 오류를 반환.
//

        };
    }
}
