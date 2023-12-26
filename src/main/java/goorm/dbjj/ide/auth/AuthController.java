package goorm.dbjj.ide.auth;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.auth.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Refresh Token 받아오면 새로운 jwt 토큰 발급
    @PostMapping("/refresh-token")
    public ApiResponse<TokenInfo> getTokens(
            @RequestHeader("Authorization-refresh") String refreshToken
    ){
        log.trace("AuthController.getTokens() 실행 : refresh-token: {}", refreshToken);

        // 'Bearer ' 접두사 제거
        String cleanToken = refreshToken.startsWith("Bearer ") ? refreshToken.substring(7) : refreshToken;

        // 새로운 토큰 발급
        TokenInfo newTokens = authService.issuingToken(cleanToken);

        // ApiResponse 객체를 통해 응답 반환
        return ApiResponse.ok(newTokens);
    }
}
