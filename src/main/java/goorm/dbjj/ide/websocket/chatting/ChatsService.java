package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.dto.ChatType;
import goorm.dbjj.ide.websocket.chatting.dto.ChatsDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChatsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatsService {
    public ChatsResponse enter(Long chatsDto) {
        log.info("ChatsService.enter execute");
        String content = chatsDto + "유저님이 참여하였습니다.";
        return ChatsResponse.builder()
                .currentUsers(1L)
                .messageType(ChatType.ENTER)
                .projectUserId(1L)
                .content(content)
                .build();
    }
    public ChatsResponse talk(ChatsDto chatsDto) {
        log.info("ChatsService.talk execute");
        return ChatsResponse.builder()
                .currentUsers(1L)
                .messageType(ChatType.TALK)
                .projectUserId(1L)
                .content(chatsDto.getContent())
                .build();
    }

}
