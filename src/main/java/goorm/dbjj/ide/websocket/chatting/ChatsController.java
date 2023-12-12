package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.dto.ChatsDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatsController {

    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;

    @MessageMapping("/project/{projectId}/chat-create")
    @SendTo("/topic/project/{projectId}/chat")
    public ChatsResponse talk(
            @DestinationVariable("projectId") Long projectId,
            @Payload ChatsDto chatsDto
    ) {
        log.info("ChattingController.chatting execute");
        return chatsService.talk(chatsDto);
    }
}
