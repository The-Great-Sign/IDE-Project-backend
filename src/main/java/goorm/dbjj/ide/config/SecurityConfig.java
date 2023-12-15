package goorm.dbjj.ide.config;

import goorm.dbjj.ide.auth.jwt.JwtFilter;
import goorm.dbjj.ide.auth.oauth2.CustomOAuth2UserService;
import goorm.dbjj.ide.auth.oauth2.handler.OAuth2LoginFailureHandler;
import goorm.dbjj.ide.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    // HTTP 보안 설정을 위한 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(configurerCustomizer()) // CORS 설정
                .csrf((csrf) -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(reqs -> reqs
                        // 인증이 되면 들어갈 수 있는 주소.
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/login").permitAll()
                        .anyRequest().permitAll())
                // 세션을 사용하지 않으므로 Stateless로 설정
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                // jwtFilter가 Username~filter보다 먼저 실행되도록 설정.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .getOrBuild(); // 마지막으로 getOrBuild()를 호출하여 SecurityFilterChain 객체 생성
    }

    /**
     * todo: 나중에 Cors 다시 설정
     * 개발 중에는 Cors 열어두기.
     */
    @Bean
    Customizer<CorsConfigurer<HttpSecurity>> configurerCustomizer() {

        return cors -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOrigin("http://localhost:3000"); // localhost:3000 에서만 요청 가능
            configuration.addAllowedMethod("*"); // GET, PUT, POST 다 가능
            configuration.addAllowedHeader("*"); // 모든 헤더 허용
            configuration.setAllowCredentials(true); // CORS 문제 해결

            // 구성된 Cors를 적용.
            cors.configurationSource(request -> configuration);
        };
    }
}