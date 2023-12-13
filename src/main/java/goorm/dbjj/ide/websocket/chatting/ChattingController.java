package goorm.dbjj.ide.websocket.chatting;

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
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChattingController {

    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;

    /**
     * 채팅방 subscribe 시 입장 알림 및 웹 소켓 세션 등록하기
     * */
    @SubscribeMapping("/project/{projectId}/chat")
    public void enter(
            SimpMessageHeaderAccessor headerAccessor,
            @DestinationVariable("projectId") Long projectId
    ){
        log.trace("ChattingController.enter execute");

        // todo : jwt? 시큐리티? 등을 이용해서 유저정보 가져오기
        Long userId = 1L;

        ChattingResponseDto enterMessage = chatsService.enter(headerAccessor, projectId, userId);
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

        // todo : jwt? 시큐리티? 등을 이용해서 유저정보 가져오기
        Long userId = 1L;

        return chatsService.talk(chatsDto, userId);
    }
}
