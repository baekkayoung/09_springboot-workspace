package com.kh.spring.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 스프링 설정 클래스라는 역할을 부여
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청 허용
                )
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .formLogin(form -> form.disable()) // 로그인 폼 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()); // HTTP Basic 인증 비활성화

        return http.build();
    }

    @Bean // PasswordEncoder라는 타입의 빈을 스프링이 관리함
    public PasswordEncoder passwordEncoder(){ // PasswordEncoder는 BCryptPasswordEncoder의 부모격
        return new BCryptPasswordEncoder();
    }


}
