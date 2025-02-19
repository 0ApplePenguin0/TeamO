package com.example.workhive.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * 시큐리티 환경설정
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    private AuthenticatedUserDetailsService authenticatedUserDetailsService;

    //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
            "/"                     //root
            , "/images/**"          //이미지 경로
            , "/css/**"             //CSS파일들
            , "/js/**"              //JavaSCript 파일들
            , "/member/**"        //회원가입, 로그인
            , "/idCheck/**"
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                .userDetailsService(authenticatedUserDetailsService)
                .authorizeHttpRequests(author -> author
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        // 회사 참여를 위한 URL
                        .requestMatchers("/register/**").hasRole("USER")
                        // 회의실 추가는 ROLE_ADMIN만 접근 가능
                        .requestMatchers("/reserve/meetingRoom/add/**").hasRole("ADMIN")
                        // 회의실 예약은 ROLE_EMPLOYEE, ROLE_MANAGER, ROLE_ADMIN 접근 가능
                        .requestMatchers("/reserve/meetingRoom/**").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        .requestMatchers("/chat/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE") // 여기에서 수정
                        .requestMatchers("/main/board").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/Message").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/MessageForm").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/SentMessage").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/ReceivedMessage").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/recycleBin").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/readReceived").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .requestMatchers("/main/board/readSent").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
            .httpBasic(Customizer.withDefaults())
            .formLogin(formLogin -> formLogin
                    .loginPage("/member/loginForm")
                    .usernameParameter("memberId")
                    .passwordParameter("memberPassword")
                    .loginProcessingUrl("/member/login" )
                    .successHandler(customLoginSuccessHandler)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
            )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedPage("/access-denied") // 접근 거부 페이지 설정
                );

        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
