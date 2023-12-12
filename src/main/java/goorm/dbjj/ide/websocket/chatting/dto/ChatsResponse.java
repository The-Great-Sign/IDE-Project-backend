package goorm.dbjj.ide.websocket.chatting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

/**
 * 채팅 웹소켓 반환 타입
 * */
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatsResponse {
    private ChatType messageType;
    private Long projectUserId;
    private String content;
    private Long currentUsers;
}
