package goorm.dbjj.ide.auth.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    /**
     * 인증 절차 자체에서 실패하였을 때 처리.
     * ex) OAuth2 제공자와의 통신 중 문제, 리디렉션 불일치 등.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("소셜로그인 실패! 로그를 확인해주세요.");
        log.debug("소셜 로그인에 실패했습니다. 에러메세지: {}",exception.getMessage());
    }
}
