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
public class ChattingResponseDto {
    private ChatType messageType;
    private Long userId; // todo : 추후 객체로 전송 해야 함 닉네임 또는 이메일 등
    private String content;
    private Long currentUsers;
}
