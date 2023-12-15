package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.websocket.WebSocketUser;
import goorm.dbjj.ide.websocket.WebSocketUserSessionMapper;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChattingController {

    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;

    /**
     * 채팅방 subscribe 시 입장 알림 및 웹 소켓 세션 등록하기
     * */
    @SubscribeMapping("/project/{projectId}/chat")
    public void enter(
            SimpMessageHeaderAccessor headerAccessor,
            @DestinationVariable("projectId") Long projectId
    ){
        log.trace("ChattingController.enter execute");
        // 세션 방식을 이용해서 유저 아이디 가져오기
        Long userId = getUserId(headerAccessor);

        ChattingResponseDto enterMessage = chatsService.enter(projectId, userId);
        template.convertAndSend("/topic/project/"+projectId+"/chat",enterMessage);
    }

    /**
     * 채팅방 subscribe 시 입장 알림
     * */
    @MessageMapping("/project/{projectId}/chat-create")
    @SendTo("/topic/project/{projectId}/chat")
    public ChattingResponseDto talk(
            SimpMessageHeaderAccessor headerAccessor,
            @DestinationVariable("projectId") Long projectId,
            @Payload ChattingContentRequestDto chatsDto
    ) {
        log.trace("ChattingController.chatting execute");

        // 세션 방식을 이용해서 유저 아이디 가져오기
        Long userId = getUserId(headerAccessor);

        return chatsService.talk(chatsDto, userId);
    }

    /**
     * headerAccessor에서 세션아이디 추출 및
     * 세션 아이디를 이용해서 유저 아이디 반환해주는 메서드
     * */
    private Long getUserId(SimpMessageHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");
        String uuid = simpSessionAttributes.get("WebSocketUserSessionId");

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(uuid);
        return webSocketUser.getUserId();
    }
}
