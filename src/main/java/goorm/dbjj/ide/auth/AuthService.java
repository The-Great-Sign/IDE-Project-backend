package goorm.dbjj.ide.auth;

import goorm.dbjj.ide.auth.jwt.JwtAuthProvider;
import goorm.dbjj.ide.auth.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtAuthProvider jwtAuthProvider;

    public TokenInfo issuingToken(String refreshToken){

        // 1. 새로운 토큰 발급
        TokenInfo newTokens = jwtAuthProvider.renewTokens(refreshToken);

        // 2. 발급된 새 리프레시 토큰을 DB에 저장
        // @Transactional 어노테이션이 있으므로 DB에 반영됩니다.

        // 3. Authentication 업데이트
        Authentication authentication = jwtAuthProvider.getAuthentication(newTokens.getAccessToken());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 클라이언트에게 던지기 위해 발급된 토큰에 Bearer 붙이기.
        newTokens.setAccessToken("Bearer "+newTokens.getAccessToken());
        newTokens.setRefreshToken("Bearer "+newTokens.getRefreshToken());

        return newTokens;
    }
}