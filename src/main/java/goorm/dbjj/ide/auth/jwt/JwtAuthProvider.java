package goorm.dbjj.ide.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
     * JWT를 사용해서 사용자 인증 정보 추출, Authentication에 담기.
     *  유저 정보가 비어있다면 exception.
     */
    public Authentication getAuthentication(String token){
        Claims claims = jwtIssuer.getClaims(token);
        String email = jwtIssuer.getSubject(claims);
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
