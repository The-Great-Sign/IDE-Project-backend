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
import org.springframework.util.ObjectUtils;
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

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    @Value("${app.exception-path}")
    private static String EXCEPTION_PATH;
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveTokenFromRequest(request);

        // EXCEPTION_PATH는 토큰 검사 안함.
        if(request.getRequestURI().equals(EXCEPTION_PATH)){
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 유효하면, 유저 정보 가져올 수 있게 저장.
        if(isTokenValidAndRefixed(token)){
            processTokenAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(accessHeader);

        if (!ObjectUtils.isEmpty(token)) {
            return token;
        }
        return null;
    }

    /**
     * 토큰이 비어있지 않고, "Bearer "로 시작하는지 확인.
     */
    private boolean isTokenValidAndRefixed(String token){
        return token != null && token.startsWith(TOKEN_PREFIX);
    }

    private void processTokenAuthentication(String token) {
        token = token.substring(TOKEN_PREFIX.length());
        log.trace("토큰 확인: {}", token);

        if (jwtAuthProvider.validateToken(token)) {
            Authentication auth = jwtAuthProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
}
