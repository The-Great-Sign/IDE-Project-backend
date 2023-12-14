package goorm.dbjj.ide.auth.jwt;

import goorm.dbjj.ide.api.exception.BaseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 인증된 사용자인지 확인하는 filter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // request에서 토큰 정보 받아옴.
        String accessToken =  request.getHeader(accessHeader);
        String refreshToken = request.getHeader(refreshHeader);

        // "Bearer " 뺀 토큰
        accessToken = HeaderUtil.getToken(accessToken);
        refreshToken = refreshToken!=null? HeaderUtil.getToken(refreshToken):null;

        // 1. accessToken만 들어왔고, 유효한 경우
        if(jwtAuthProvider.validateToken(accessToken)){
            Authentication auth = jwtAuthProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // 2. accessToken이 유효하지 않고, refreshToken이 유효한 경우
        else if(refreshToken != null && jwtAuthProvider.validateToken(accessToken,refreshToken)){
            String newToken = jwtIssuer.createToken(refreshToken);
            response.setHeader(accessHeader, newToken);

            Authentication auth = jwtAuthProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // 3. 모두 유효하지 않은 경우
        else{
            throw new BaseException("토큰이 모두 유효하지 않습니다.");
            }

        filterChain.doFilter(request, response);
    }


}
