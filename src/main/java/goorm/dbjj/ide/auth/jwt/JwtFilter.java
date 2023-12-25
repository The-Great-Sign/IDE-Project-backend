package goorm.dbjj.ide.auth.jwt;

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
import java.time.LocalDateTime;
import java.util.Base64;

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

    @Value("${app.exception-path1}")
    private String EXCEPTION_PATH1;

    @Value("${app.exception-path2}")
    private String EXCEPTION_PATH2;

    @Value("${app.exception-path3}")
    private String EXCEPTION_PATH3;

    @Value("${app.exception-path4}")
    private String EXCEPTION_PATH4;

    @Value("${app.exception-path5}")
    private String EXCEPTION_PATH5;

    @Value("${app.exception-path6}")
    private String EXCEPTION_PATH6;


    @Value("${jwt.secretKey}")
    private String secretKey;

    private final static String TOKEN_PREFIX = "Bearer ";

    @PostConstruct
    void init(){
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
        if(request.getRequestURI().contains(EXCEPTION_PATH1)){
            log.trace("이 페이지는 검사 안함 : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().contains(EXCEPTION_PATH2)){
            log.trace("이 페이지는 검사 안함 : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().contains(EXCEPTION_PATH3)){
            log.trace("이 페이지는 검사 안함 : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().contains(EXCEPTION_PATH4)){
            log.trace("이 페이지는 검사 안함 : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().contains(EXCEPTION_PATH5)){
            log.trace("이 페이지는 검사 안함 : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().contains(EXCEPTION_PATH6)){
            log.trace("이 페이지는 검사 안함 : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = request.getHeader(accessHeader);
        String refreshToken = request.getHeader(refreshHeader);

        /**
         * 1. AccessToken이 들어오는 경우.
         */
        if(isTokenPrefixed(accessToken)){ // "Bearer "로 시작하는지 확인.

            // 토큰만 추출. (Bearer 뒤에 부분)
            accessToken = accessToken.substring(TOKEN_PREFIX.length());
            log.trace("액세스 토큰 확인: {}", accessToken);

            // 토큰이 만료되었는지 확인.
            if(!isValidateToken(request, response, accessToken)){
                return; // 토큰 검증 실패로 더이상 filter 검사를 하지 않는다.
            }

            Authentication auth = jwtAuthProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        /**
         * 2. RefreshToken이 들어온 경우.
         * Refresh 토큰이 유효하면 -> Access, Refresh 토큰 둘다 재발급.
         */
        else if(isTokenPrefixed(refreshToken)){ // "Bearer "로 시작하는지 확인.

            // 토큰만 추출. (Bearer 뒤에 부분)
            refreshToken = refreshToken.substring(TOKEN_PREFIX.length());
            log.trace("리프레시 토큰 확인: {}", refreshToken);

            // 토큰이 만료되었는지 확인.
            if(!isValidateToken(request, response, accessToken)){
                return; // 토큰 검증 실패로 더이상 filter 검사를 하지 않는다.
            }

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
        else{
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없거나 잘못된 토큰 정보입니다.", request.getRequestURI());
            log.debug("토큰이 없거나 잘못된 토큰 정보 입니다.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 토큰이 비어있지 않고, "Bearer "로 시작하는지 확인.
    private boolean isTokenPrefixed(String token){
        return token != null && token.startsWith(TOKEN_PREFIX);
    }

    // 토큰 유효성 검증
    private boolean isValidateToken(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {

        Claims claims;
        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return true; // 검증 성공

        } catch (Exception e) {
            handleException(response, request, determineStatusCode(e), determineErrorMessage(e), e);
            return false; // 검증 실패
        }
    }

    // Exception 처리 메서드
    private void handleException(HttpServletResponse response, HttpServletRequest request, int statusCode, String message, Exception e) throws IOException {
        log.debug(message + ": " + e.getMessage()); // 로그 추가
        sendErrorResponse(response, statusCode, message, request.getRequestURI());
    }

    // 예외에 따른 상태 코드 결정
    private int determineStatusCode(Exception e) {
        if (e instanceof ExpiredJwtException) return HttpServletResponse.SC_UNAUTHORIZED;
        if (e instanceof MalformedJwtException || e instanceof UnsupportedJwtException || e instanceof IllegalArgumentException) {
            return HttpServletResponse.SC_BAD_REQUEST;
        }
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    // 예외에 따른 에러 메시지 결정
    private String determineErrorMessage(Exception e) {
        if (e instanceof ExpiredJwtException) return "토큰이 만료되었습니다.";
        if (e instanceof MalformedJwtException) return "액세스 토큰이 유효하지 않습니다.";
        if (e instanceof SignatureException) return "토큰 서명이 올바르지 않습니다.";
        if (e instanceof UnsupportedJwtException) return "지원하지 않는 토큰입니다.";
        if (e instanceof IllegalArgumentException) return "토큰 내용이 비어있습니다.";
        return "시스템 내부에서 오류가 발생하였습니다.";
    }

    // 응답 처리를 위한 메서드
    private void sendErrorResponse(HttpServletResponse response, int status, String message, String path) throws IOException {
        String jsonResponse = String.format(
                "{\"status\": %d, \"path\": \"%s\", \"message\": \"%s\", \"timestamp\": \"%s\"}",
                status, path, message, LocalDateTime.now()
        );
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        response.getWriter().close();
    }
}