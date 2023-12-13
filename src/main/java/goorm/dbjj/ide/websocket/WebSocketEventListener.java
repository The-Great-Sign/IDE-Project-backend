package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.websocket.chatting.ChatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    // 현재 인원 관리를 여기서 하면 되네.
    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // TODO: 여기서 연결 관련 로직을 구현합니다.
        log.trace("WebSocketEventListener.handleWebSocketConnectListener execute");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // TODO: 여기서 연결 해제 관련 로직을 구현합니다.
        log.trace("WebSocketEventListener.handleWebSocketDisconnectListener execute");
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        log.trace("handleSubscribeEvent execute");
    }
}