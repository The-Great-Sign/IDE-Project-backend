package goorm.dbjj.ide.auth.oauth2.handler;

import goorm.dbjj.ide.auth.jwt.JwtIssuer;
import goorm.dbjj.ide.auth.jwt.TokenInfo;
import goorm.dbjj.ide.auth.oauth2.CustomOAuth2User;
import goorm.dbjj.ide.domain.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtIssuer jwtIssuer;
    private final UserRepository userRepository;

    private static final String BEARER = "Bearer ";

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            log.trace("OAuth2 Login 성공! social PK 확인 {}",oAuth2User.getName());

            // 로그인에 성공해서 access, refresh 토큰 생성.
            loginSuccess(response, oAuth2User);

        } catch (Exception e){ // OAuth2 인증 후 처리에서 문제 발생.
            log.debug("로그인 실패", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "로그인 처리 중 오류가 발생했습니다.");
        }
    }

    /**
     *  로그인 성공 시 access, refresh 토큰 생성
      */
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {

        TokenInfo tokenInfo = jwtIssuer.createToken(oAuth2User.getEmail(),"ROLE_USER");


        log.trace("액세스 토큰 발행 : {}",tokenInfo.getAccessToken());
        log.trace("리프레시 토큰 발행 : {}", tokenInfo.getRefreshToken());

        // 리프레시 토큰 저장.
        sendAccessAndRefreshToken(response, tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());
        updateRefreshToken(oAuth2User.getEmail(), tokenInfo.getRefreshToken());

        // HTTP 상태 코드 설정
        response.setStatus(HttpServletResponse.SC_OK);

        // 콘텐츠 타입 설정
        response.setContentType("application/json");

        // JSON 응답 본문 생성 (예시)
        String responseBody = "{\"message\": \"Login successful\"}";

        // 응답 본문 전송
        response.getWriter().write(responseBody);
        response.getWriter().flush();
        response.getWriter().close();
    }

    private void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken){
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, BEARER + accessToken);
        response.setHeader(refreshHeader, BEARER+refreshToken);
        log.trace("Access Token, Refresh Token 헤더 설정 완료");
    }

    public void updateRefreshToken(String email, String refreshToken) {
        log.trace("리프레시 토큰 정보 : {}",refreshToken);
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다.")
                );
    }
}
