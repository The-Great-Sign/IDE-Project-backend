package goorm.dbjj.ide.websocket.jwt;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.auth.jwt.JwtAuthProvider;
import goorm.dbjj.ide.auth.jwt.JwtIssuer;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketJwt {
    // jwt
    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;
    private final UserRepository userRepository;
    private final String TOKEN_PREFIX = "Bearer ";

    /**
     *  유효한 JWT에서 사용자 정보를 가져오는 로직
     * */
    public User getUserFromValidJwt(String accessToken ) {
        log.trace("액세스 토큰 : {}", accessToken);

        if (accessToken == null || !accessToken.startsWith(TOKEN_PREFIX)) {
            log.debug("유효한 토큰 형식이 아닙니다. : {}", accessToken);
            throw new BaseException("유효한 토큰 형식이 아닙니다.");
        }

        // 토큰만 추출.
        accessToken = accessToken.substring(TOKEN_PREFIX.length());

        // 토큰이 유효한지 검증.
        if (!jwtAuthProvider.validateToken(accessToken)) {
            log.debug("토큰이 유효하지 않습니다. : {}", accessToken);
            throw new BaseException("토큰이 유효하지 않습니다.");
        }

        // 토큰을 사용해서 유저 정보 가져오기.
        String userEmail = jwtIssuer.getSubject(jwtIssuer.getClaims(accessToken));
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BaseException("해당 이메일을 가진 유저가 없습니다."));
    }
}
