package goorm.dbjj.ide.websocket;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 구독 정보를 저장하는 세션 저장소
 * */
@Component
public class SubscribeSessionMapper {
    private Map<String, WebSocketUserSession> sessions = new ConcurrentHashMap<>();

    /**
     * @param webSocketSession, userId, projectId, subscribeType
     * @return Mapper에 WebSocketUserSession정보 추가하기
     * */
    public void addSession(SimpMessageHeaderAccessor webSocketSession, Long userId, Long projectId, SubscribeType subscribeType) {
        sessions.put(webSocketSession.getSessionId(), new WebSocketUserSession(webSocketSession.getSessionId(), userId, projectId,subscribeType));
    }

    /**
     * @param eventSessionId
     * @return Mapper에 WebSocketUserSession정보 삭제하기
     * */
    public void removeSession(String eventSessionId) {
        sessions.remove(eventSessionId);
    }

    /**
     * @param eventSessionId
     * @return WebSocketUserSession 을 받을 수 있음.
     * */
    public Optional<WebSocketUserSession> getSession(String eventSessionId) {
        return Optional.ofNullable(sessions.get(eventSessionId));
    }
}
