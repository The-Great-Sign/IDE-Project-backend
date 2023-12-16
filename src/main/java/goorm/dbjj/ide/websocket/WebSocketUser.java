package goorm.dbjj.ide.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 웹소켓 세션에 포함될 내용
 * */
@Slf4j
@Getter
public class WebSocketUser {
    private final Long userId;
    private final Long projectId;
    private final Set<String> subscribes;

    public WebSocketUser(Long userId, Long projectId) {
        this.userId = userId;
        this.projectId = projectId;
        this.subscribes = ConcurrentHashMap.newKeySet(); // 키를 이용한 Set 구현.
    }

    public void startSubscribe(String subscribeType){
        this.subscribes.add(subscribeType);
    }

    /**
     * 구독하고 있는지 확인하는 메서드
     * */
    public boolean isSubscribe(String subscribeType){
        log.info("subscribeType {}", subscribeType);
        
        // 구독하고 있다면
        if(this.subscribes.stream().anyMatch(s -> s.equals(subscribeType))){
            return true;
        }
        // 구독 안하고 있다면
        return false;
    }
}
