package goorm.dbjj.ide.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.api.exception.UnAuthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
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
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

/**
 * 인증된 사용자인지 확인하는 filter
 * OncePerRequestFilter: 요청마다 한번씩 실행되는 필터.
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

    @Value("${app.exception-paths}")
    private List<String> EXCEPTION_PATHS;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final ObjectMapper objectMapper;


    private final static String TOKEN_PREFIX = "Bearer ";

    @PostConstruct
    void init() {
        // secretKey 한번 더 암호화.
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        //get IP
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = request.getRemoteAddr();

        log.trace("IP : {}", ip);


        // EXCEPTION_PATH는 토큰 검사 안함.
        if (EXCEPTION_PATHS.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader(accessHeader);
        String refreshToken = request.getHeader(refreshHeader);

        /**
         * 토큰 검증에서 에러가 발생한 경우
         * sendErrorResponse를 통해 응답 처리
         */
        try {
            /**
             * 1. AccessToken이 들어오는 경우.
             */
            if (isTokenPrefixed(accessToken)) { // "Bearer "로 시작하는지 확인.

                // 토큰만 추출. (Bearer 뒤에 부분)
                accessToken = accessToken.substring(TOKEN_PREFIX.length());
                log.trace("액세스 토큰 확인: {}", accessToken);

                // 토큰이 만료되었는지 확인.
                validateToken(request, response, accessToken);

                Authentication auth = jwtAuthProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            /**
             * 2. RefreshToken이 들어온 경우.
             * Refresh 토큰이 유효하면 -> Access, Refresh 토큰 둘다 재발급.
             */
            else if (isTokenPrefixed(refreshToken)) { // "Bearer "로 시작하는지 확인.

                // 토큰만 추출. (Bearer 뒤에 부분)
                refreshToken = refreshToken.substring(TOKEN_PREFIX.length());
                log.trace("리프레시 토큰 확인: {}", refreshToken);

                // 토큰이 만료되었는지 확인.
                validateToken(request, response, refreshToken);

                // 새로 발급 받은 토큰 -> response에 넘기기.
                TokenInfo newTokens = jwtAuthProvider.renewTokens(refreshToken);

                // 토큰을 response header에 담음.
                response.setHeader(accessHeader, TOKEN_PREFIX + newTokens.getAccessToken());
                response.setHeader(refreshHeader, TOKEN_PREFIX + newTokens.getRefreshToken());

                // 인증된 사용자의 정보를 저장.
                Authentication auth = jwtAuthProvider.getAuthentication(newTokens.getAccessToken());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            /**
             * 3. 토큰이 없는 경우.
             */
            else {
                throw new UnAuthorizedException("토큰이 없거나 잘못된 토큰 정보 입니다.");
            }

        } catch (UnAuthorizedException e) { //
            sendErrorResponse(response, e.getMessage());
            return; //filterChain 종료
        }
        filterChain.doFilter(request, response);
    }

    // 토큰이 비어있지 않고, "Bearer "로 시작하는지 확인.
    private boolean isTokenPrefixed(String token) {
        return token != null && token.startsWith(TOKEN_PREFIX);
    }

    // 토큰 유효성 검증
    private void validateToken(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {

        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new UnAuthorizedException("토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            throw new UnAuthorizedException("유효하지 않은 토큰입니다.");
        } catch (SignatureException e) {
            throw new UnAuthorizedException("토큰 서명이 올바르지 않습니다.");
        } catch (UnsupportedJwtException e) {
            throw new UnAuthorizedException("지원하지 않는 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            throw new UnAuthorizedException("토큰 내용이 비어있습니다.");
        } catch (Exception e) {
            throw new UnAuthorizedException("시스템 내부에서 오류가 발생하였습니다.");
        }
    }

    // 응답 처리를 위한 메서드
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //ApiResponse 꼴에 맞춰서 전송
        ApiResponse<Object> resp = ApiResponse.fail(message);
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(resp));
        writer.flush();
    }
}