package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

/**
 *  HandshakeInterceptor
 *  웹소켓 Connect 시 가장 먼저 동작하는 인터셉터입니다.
 *  Http Upgrade 동작 시에 인증된 사용자가 인증된 프로젝트에 접근하는지 검증하고
 *  WebSocketUserSessionMapper에 key, value(UserInfo,ProejctId)를 저장하는 인터셉터
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandShackInterceptor implements HandshakeInterceptor {
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * http Upgrade 응답 이전에 유효한 유저인지 jwt 를 확인하고, WebSocketUserSessionMapper에 등록하는 메서드
     * */
    @Override
    @Transactional(readOnly = true)
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String sessionId = UUID.randomUUID().toString();

        attributes.put("WebSocketUserSessionId", sessionId);
        attributes.put("uri", request.getURI().toString());
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
