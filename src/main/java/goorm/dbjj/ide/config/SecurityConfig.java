package goorm.dbjj.ide.config;

import goorm.dbjj.ide.auth.oauth2.CustomOAuth2UserService;
import goorm.dbjj.ide.auth.oauth2.handler.OAuth2LoginFailureHandler;
import goorm.dbjj.ide.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import goorm.dbjj.ide.domain.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(corsConfigurationSource()) // CORS 설정
                .csrf((csrf) -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(reqs -> reqs
                        // 인증이 되면 들어갈 수 잇는 주소.
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .getOrBuild(); // 마지막으로 getOrBuild()를 호출하여 SecurityFilterChain 객체 생성
    }

    @Bean
    Customizer<CorsConfigurer<HttpSecurity>> corsConfigurationSource() {

        return cors -> {
            CorsConfiguration configuration = new CorsConfiguration();
            // 허용된 출처, 메소드, 헤더를 설정.
            configuration.addAllowedOrigin("http://localhost:8080");
            configuration.addAllowedMethod("GET");
            configuration.addAllowedMethod("POST");
            configuration.addAllowedMethod("OPTIONS");
            configuration.addAllowedMethod("PUT");
            configuration.addAllowedMethod("DELETE");
            configuration.addAllowedHeader("*");

            // 구성된 Cors를 적용.
            cors.configurationSource(request -> configuration);

        };
    }
}
