package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

/**
 *  HandshakeInterceptor 
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandShackInterceptor implements HandshakeInterceptor {
//  todo: UserReposity 조회해야합니다.   private final UserRepository userRepository;
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    
    /**
     * http Upgrade 응답 이전에 유효한 유저인지 jwt 를 확인하고, WebSocketUserSessionMapper에 등록하는 메서드
     * */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // URL 마지막 부분에 존재하는 프로젝트ID 가져오기
        String[] split = request.getURI().toString().split("/");
        Long projectId = Long.parseLong(split[split.length-1]);
        log.trace("{}", projectId);

        //todo : jwt UserId 가져와야함.
        // 채팅서비스나 이런데서 db 조회 해서 dto 짚어넣기
        // userRepository.findByEmail(email);

         // SecuritycontextHolder을 사용해서 인증된 사용자의 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자인지 확인, 아니면 exception.
        if(authentication == null && !authentication.isAuthenticated()){
            throw new BaseException("인증된 사용자가 아닙니다.");
        }

        // todo: 이후 User -> CustomUserDetails로 교체 예정.
        // 인증된 사용자의 세부 정보 불러오기.
        User userDetails = (User) authentication.getPrincipal();
        Long userId = userDetails.getId();
        log.trace("웹소켓 사용자 ID, 이름 가져오기 : {} {}", userId, userDetails.getNickname());

        /**
         * 현재는 User Entity 전체에 연결되어 있으므로
         * userDetails.get~() 이런식으로 유저정보 불러와서 사용가능합니다.
         */

        //todo : userId 및 projectId가 일치하는 .ProjectUser가 있는지 확인

        // 세션등록
        String uuid = UUID.randomUUID().toString();
        webSocketUserSessionMapper.put(uuid, new WebSocketUser(userId, projectId));

        // STOMP 메서드를 보내면 사용자 인식 가능하게 함.
        attributes.put("WebSocketUserSessionId", uuid);
         return true;
    }

    /**
     * http Upgrade 응답 이후 실행되는 부분
     * 사용 안함.
     * */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
