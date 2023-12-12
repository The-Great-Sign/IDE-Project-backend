package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.websocket.chatting.dto.ChatType;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatsService {

    /**
     * 클라이언트가 채팅방입장시 enter 메서드 수행
     * */
    public ChattingResponseDto enter(Long chatsDto) {
        log.trace("ChatsService.enter execute");
        String content = chatsDto + "유저님이 참여하였습니다.";
        return ChattingResponseDto.builder()
                .currentUsers(1L)
                .messageType(ChatType.ENTER)
                .projectUserId(1L)
                .content(content)
                .build();
    }

    /**
     * 클라이언트가 채팅 send시 모든 클라이언트에게 브로드캐스팅으로 talk
     * */
    public ChattingResponseDto talk(ChattingContentRequestDto chatsDto) {
        log.trace("ChatsService.talk execute");
        return ChattingResponseDto.builder()
                .currentUsers(1L)
                .messageType(ChatType.TALK)
                .projectUserId(1L)
                .content(chatsDto.getContent())
                .build();
    }

}
