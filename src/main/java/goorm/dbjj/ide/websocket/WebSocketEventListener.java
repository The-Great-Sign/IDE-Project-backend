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
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // TODO: 여기서 연결 해제 관련 로직을 구현합니다.
        System.out.println("Disconnected web socket connection");
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        log.info("handleSubscribeEvent execute");

        String destinationURI = (String) event.getMessage().getHeaders().get("simpDestination");
        //TODO : 구독 시 현재 동작 과정 정리하기
        int num = Integer.parseInt(destinationURI.split("/")[3]);
        log.info("{}",destinationURI.split("/")[3]);
        log.info("{}",destinationURI.split("/")[4]);

        //TODO : 현재 프로젝트에 접근하고자하는 USERID 받아오기!
        Long userId = 0L;

        if(destinationURI.split("/")[4].equals("chat")) {
            template.convertAndSend(destinationURI, chatsService.enter(userId));
        }
    }
}