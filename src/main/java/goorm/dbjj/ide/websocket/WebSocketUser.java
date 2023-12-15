package goorm.dbjj.ide.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 웹소켓 세션에 포함될 내용
 * */
@Getter
public class WebSocketUser {
    private Long userId;
    private Long projectId;
    private boolean isChattingSubscribe; // false : 구독 안함, true : 구독함
    private boolean isTerminal; // false : 구독 안함, true : 구독함
    private boolean isCursor; // false : 구독 안함, true : 구독함

    public WebSocketUser(Long userId, Long projectId) {
        this.userId = userId;
        this.projectId = projectId;
        this.isChattingSubscribe = false;
        this.isTerminal = false;
        this.isCursor = false;
    }

    public void SubscribeChatting(){
        this.isChattingSubscribe = true;
    }

    public void SubscribeTerminal(){
        this.isTerminal = true;
    }

    public void SubscribeCursor(){
        this.isCursor = true;
    }
}
