package goorm.dbjj.ide.auth.jwt;

import lombok.extern.slf4j.Slf4j;

/**
 * AccessToken이 비어있는지 확인하고, 비어있지 않으면 prefix 떼서 넘김.
 */
@Slf4j
public class HeaderUtil {
    private final static String TOKEN_PREFIX = "Bearer ";

    public static String getToken(String fullToken){

        if(fullToken == null){
            log.debug("헤더에 토큰이 없습니다.");
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        if(!fullToken.startsWith(TOKEN_PREFIX)){
            log.debug("인증되지 않은 토큰입니다.");
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        return fullToken.substring(TOKEN_PREFIX.length());
    }
}
