package goorm.dbjj.ide.auth.oauth2.handler;

import goorm.dbjj.ide.domain.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.debug("OAuth2 Login 성공!");

        try{
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            log.info("너는 누구냐? {}",oAuth2User.getName());

            // 로그인에 성공해서 access, refresh 토큰 생성.
            loginSuccess(response, oAuth2User);

        } catch (Exception e){
            throw e;
        }
    }

    /**
     * TODO: 로그인 완료 시, access, refresh 토큰 발행.
     * 지금은 임시 리디렉션 걸어놈. 회원이면 들어갈 수 있는 페이지로 이동.
      */
    private void loginSuccess(HttpServletResponse response, DefaultOAuth2User oAuth2User) throws IOException {

        // 리디렉션할 URL 설정
        String redirectUrl = "http://localhost:8080/user/main";

        // 임시로 리디렉션 추가. 회원이면 들어갈 수 있는 페이지로 이동.
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 상태코드
        response.setHeader("Location", redirectUrl);

        response.flushBuffer();
    }
}
