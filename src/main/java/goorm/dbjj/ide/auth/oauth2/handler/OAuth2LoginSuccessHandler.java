package goorm.dbjj.ide.auth.oauth2.handler;

import goorm.dbjj.ide.domain.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.authorization-redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        try{
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            log.trace("OAuth2 Login 성공! social PK 확인 {}",oAuth2User.getName());

            // 로그인에 성공해서 access, refresh 토큰 생성.
            loginSuccess(response, oAuth2User);

        } catch (Exception e){ // OAuth2 인증 후 처리에서 문제 발생.
            log.debug("로그인 실패", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     * TODO: 로그인 완료 시, access, refresh 토큰 발행.
     * 지금은 임시 리디렉션 걸어 놓음. 회원이면 들어갈 수 있는 페이지로 이동.
      */
    private void loginSuccess(HttpServletResponse response, DefaultOAuth2User oAuth2User) throws IOException {
        // 임시로 리디렉션 추가. 회원이면 들어갈 수 있는 페이지로 이동.
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 상태코드
        response.setHeader("Location", redirectUrl);
    }
}
