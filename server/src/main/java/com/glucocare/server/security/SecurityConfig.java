package com.glucocare.server.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * JWT 기반 인증 및 보안 설정을 구성
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final InvalidAuthEntryPoint invalidAuthEntryPoint;

    /**
     * Spring Security 필터 체인 설정
     * CORS, CSRF, 인증 및 인가 규칙을 정의
     *
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 오류
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers(HttpMethod.OPTIONS, "/**")
                                                                                   .permitAll()
                                                                                   .requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**")
                                                                                   .permitAll()
                                                                                   .requestMatchers("/api/members/login", "/api/members/register")
                                                                                   .permitAll()
                                                                                   .anyRequest()
                                                                                   .authenticated())
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(it -> it.authenticationEntryPoint(invalidAuthEntryPoint));
        return http.build();
    }

    /**
     * 비밀번호 암호화 빈 등록
     * BCrypt 알고리즘을 사용하여 비밀번호를 암호화
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
