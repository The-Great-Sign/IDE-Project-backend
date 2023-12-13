package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.websocket.chatting.ChatsService;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;
    private final SubscribeSessionMapper subscribeSessionMapper;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // TODO: 여기서 연결 관련 로직을 구현합니다.
        log.trace("WebSocketEventListener.handleWebSocketConnectListener execute");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.trace("WebSocketEventListener.handleWebSocketDisconnectListener execute");
        String eventSessionId = event.getSessionId();
        // 구독 저장소에 있는지 확인
        WebSocketUserSession webSocketUserSession = subscribeSessionMapper.getSession(eventSessionId).orElseThrow(() -> new BaseException("이미 종료된 웹소켓입니다."));

        // 구독 저장소에 세션 삭제하기 및 종료
        subscribeSessionMapper.removeSession(eventSessionId);

        // 만약에 채팅이라면 퇴장 문구 알림
        if(webSocketUserSession.getSubscribeType().equals(SubscribeType.CHATTING)){
            Optional<ChattingResponseDto> exitMessage = chatsService.exit(webSocketUserSession);

            // 채팅창에 인원이 존재할경우에만 exitMessage를 채팅방으로 전송.
            if(exitMessage.isPresent()) {
                template.convertAndSend("/topic/project/" + webSocketUserSession.getProjectId() + "/chat",exitMessage);
            }
        }
        

    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        log.trace("handleSubscribeEvent execute");
    }
}