package goorm.dbjj.ide.websocket.chatting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * 채팅 웹소켓 반환 타입
 * */
@Builder
@Getter
public class ChattingResponseDto {
    private ChatType messageType;
    private String userNickname;
    private String content;
    private Long currentUsers;
}
