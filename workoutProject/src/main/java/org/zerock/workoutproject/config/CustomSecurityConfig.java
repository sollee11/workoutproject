package org.zerock.workoutproject.config;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {

  //스프링 시큐리티 기본 설정
  // 모든 페이지에 로그인이 필요, 로그인 성공시 / 경로로 이동

  // 패스워드 암호화 방식 설정
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  //스프링 시큐리티 커스텀 설정 메서드
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("---------------------configure-----------------------");

    http.formLogin().loginPage("/member/login");
    http.csrf().disable();
    http.logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .addLogoutHandler((request, response, authentication) -> {
              HttpSession session = request.getSession();
              session.invalidate();
            })
            .logoutSuccessHandler(((request, response, authentication) ->
                    response.sendRedirect("/")))
            .deleteCookies("id", "token"));

//        http
//                .httpBasic(httpBasicConfigurer -> httpBasicConfigurer.disable())  // HTTP Basic 인증 비활성화
//                .csrf(CsrfConfigurer::disable)
//                .cors(corsConfigurer -> corsConfigurer.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
//                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//                .authorizeHttpRequests(authorizeRequestsCustomizer -> authorizeRequestsCustomizer
//                        .requestMatchers("/**").permitAll()
//                        .anyRequest().authenticated()
//                );

//    // form태그를 사용한 로그인
//    http.formLogin().loginPage("/member/login");
//    // CSRF 끄기 설정
    http.csrf().disable();
//    // 자동로그인 설정
//    http.rememberMe()
//        .key("12345678")
//        .tokenRepository(persistentTokenRepository())
//        .userDetailsService(userDetailsService)
//        .tokenValiditySeconds(60*60*24*30);
//    // 403에러 예외처리
//    http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
//    // oauth2 로그인 페이지 설정
//    http.oauth2Login()
//        .loginPage("/member/login")
//        .successHandler(authenticationSuccessHandler());

    return http.build();
  }

}










