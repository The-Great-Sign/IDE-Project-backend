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
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP 이벤트 리스너
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;

    /**
     * DisConnect 시 채팅방 퇴장 알림 기능 구현 및 WebSocketUserSessionMapper에 존재하는 유저 정보 없애기!
     * */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.trace("WebSocketEventListener.handleWebSocketDisconnectListener execute");

        // simpSessionAttributes에 존재하는 uuid 가져오기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");
        String uuid = simpSessionAttributes.get("WebSocketUserSessionId");

        // WebSocketUserSessionMapper 없애기
        WebSocketUser removeWebSocketUser = webSocketUserSessionMapper.remove(uuid);
        if(removeWebSocketUser == null){
            log.warn("WebSocketChannelInterceptor.preSend 잘못된 사용자 접근입니다.");
            throw new BaseException("잘못된 사용자 접근");
        }

        // 퇴장 메세지 출력
        Long userId = removeWebSocketUser.getUserId();
        Long projectId = removeWebSocketUser.getProjectId();
        Optional<ChattingResponseDto> exitMessage = chatsService.exit(userId, projectId);
        if(exitMessage.isPresent()) {
            template.convertAndSend("/topic/project/"+ projectId + "/chat", exitMessage);
        }
    }

}