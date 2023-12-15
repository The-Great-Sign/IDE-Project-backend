package goorm.dbjj.ide.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

    private final UserDetailsService userDetailsService;
    private final JwtIssuer jwtIssuer;

    // 토큰 인증
    public boolean validateToken(String token){

        if(!StringUtils.hasText(token)){
            log.debug("validateToken() : 토큰이 비어있습니다.");
            return false;
        }
        Claims claims = jwtIssuer.getClaims(token);
        if(claims == null){
            log.debug("validateToken() : 유저 정보가 비어있습니다.");
            return false;
        }
        return true;
    }

    /**
     * accessToken, refreshToken 재발급
     * (accessToken, refreshToken 같이 들어오면
     * accessToken이 만료된 걸로 판단.)
     */
    public boolean validateToken(String accessToken, String refreshToken){
        // 토큰들이 비어있지 않고,
        if(!StringUtils.hasText(accessToken)
        || !StringUtils.hasText(refreshToken)){
            return false;
        }

        Claims accessClaims = jwtIssuer.getClaims(accessToken);
        Claims refreshClaims = jwtIssuer.getClaims(refreshToken);

        // 토큰의 유저 정보가 일치하면 재발급 인증.
        return accessClaims != null && refreshClaims != null
                && jwtIssuer.getSubject(accessClaims).equals(jwtIssuer.getSubject(refreshClaims));
    }

    /**
     * JWT를 사용해서 사용자 인증 정보 추출, Authentication에 담기.
     */
    public Authentication getAuthentication(String token){
        Claims claims = jwtIssuer.getClaims(token);
        String email = jwtIssuer.getSubject(claims);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(
                userDetails,null, userDetails.getAuthorities()
        );
    }
}
