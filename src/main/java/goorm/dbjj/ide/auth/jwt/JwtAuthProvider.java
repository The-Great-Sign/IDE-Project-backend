package goorm.dbjj.ide.auth.jwt;

import goorm.dbjj.ide.api.exception.UnAuthorizedException;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.User;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 토큰 자체를 인증하고, 권한 부여.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

    private final UserDetailsService userDetailsService;
    private final JwtIssuer jwtIssuer;
    private final UserRepository userRepository;

    // 토큰 인증
    public boolean validateToken(String token){

        if(!StringUtils.hasText(token)){
            log.debug("validateToken() : 토큰이 비어있습니다.");
            return false;
        }

        //jwt에 저장한 정보 추출 + 만료된 토큰인지 확인
        Claims claims = jwtIssuer.getClaims(token);

        if(claims == null){
            log.debug("validateToken() : 유저 정보가 비어있습니다.");
            return false;
        }
        return true;
    }

    // refreshToken이 들어왔을 때, 토큰과 일치하는 user의 JWT 토큰 재발급.
    @Transactional
    public TokenInfo renewTokens(String refreshToken){

        Claims claims = jwtIssuer.getClaims(refreshToken);
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UnAuthorizedException("토큰과 일치하는 유저가 없습니다."));

        log.trace("{} <-> {}", user.getEmail(), claims.getSubject());

        // 리프레시 토큰의 유저가 db에 있는지 확인.
        if(!claims.getSubject().equals(user.getEmail())){
            throw new UnAuthorizedException("회원 정보가 맞지 않습니다.");
        }

        // 새로운 토큰 발급.
        TokenInfo newTokens = jwtIssuer.createToken(user.getEmail(), user.getRole().getKey());

        // 유저 RefreshToken 업데이트.
        user.updateRefreshToken(newTokens.getRefreshToken());

        return newTokens;
    }

    /**
     * JWT를 사용해서 사용자 인증 정보 추출, Authentication에 담기.
     */
    public Authentication getAuthentication(String token){
        String email = jwtIssuer.getSubject(jwtIssuer.getClaims(token));
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 유저가 null 일 경우 exception
        if (userDetails == null) {
            log.debug("유저 정보가 없습니다. : {}" , email);
            throw new UsernameNotFoundException("유저 정보가 없습니다");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,null, userDetails.getAuthorities()
        );
    }
}