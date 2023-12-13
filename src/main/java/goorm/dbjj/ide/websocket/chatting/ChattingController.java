package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChattingController {

    private final ChatsService chatsService;
    private final SimpMessagingTemplate template;

    @SubscribeMapping("/project/{projectId}/chat")
    public void enter(
            @DestinationVariable("projectId") Long projectId
    ){
        log.trace("ChattingController.enter execute");
        //todo: 유저 매핑하는 것 해야한다.
        Long userId = 1L;
        template.convertAndSend("/topic/project/"+projectId+"/chat", chatsService.enter(userId));
    }

    @MessageMapping("/project/{projectId}/chat-create")
    @SendTo("/topic/project/{projectId}/chat")
    public ChattingResponseDto talk(
            @DestinationVariable("projectId") Long projectId,
            @Payload ChattingContentRequestDto chatsDto
    ) {
        log.trace("ChattingController.chatting execute");
        return chatsService.talk(chatsDto);
    }
}
