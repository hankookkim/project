package com.example.tokenlogin.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component //Spring이 관리하는 빈(Bean)으로 등록하기 위한 기본적인 어노테이션
@ConfigurationProperties("jwt") //application.yml 또는 application.properties 파일에 정의된 설정 값을 객체에 자동으로 매핑
public class JwtProperties {
    private String issuer;
    private String secretKey;

    //JwtProperties 클래스는 Spring Boot에서 설정 파일(application.yml 또는
    // application.properties)의 값을 객체로 매핑하는 역할
//    JwtProperties를 빈으로 등록했기 때문에
//    다른 클래스에서 @Autowired 또는 생성자 주입을 통해 사용할 수 있음.



//    @Component는 클래스 위에 붙여서 자동으로 빈 등록.
//    @Bean은 개발자가 @Configuration 클래스에서 수동으로 빈을 등록.
}
