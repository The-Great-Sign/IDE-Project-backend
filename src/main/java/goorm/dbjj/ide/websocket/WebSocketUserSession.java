package goorm.dbjj.ide.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 웹소켓 세션에 포함될 내용
 * */
@AllArgsConstructor
@Getter
public class WebSocketUserSession {
    private Long userId;
    private Long projectId;
}
