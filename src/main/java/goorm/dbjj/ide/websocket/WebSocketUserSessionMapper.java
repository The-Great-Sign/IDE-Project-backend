package goorm.dbjj.ide.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * key : uuid
 * value : WebSocketUser(projectId, UserId)
 * */
@Slf4j
@Component
public class WebSocketUserSessionMapper {
    private final ConcurrentHashMap<String, WebSocketUser> webSocketUserSessionMap;

    public WebSocketUserSessionMapper() {
        this.webSocketUserSessionMap = new ConcurrentHashMap<>();
    }

    public void put(String uuid, WebSocketUser webSocketUser){
        this.webSocketUserSessionMap.put(uuid, webSocketUser);
    }

    public WebSocketUser get(String uuid){
        log.trace("WebSocketUser : {}", this.webSocketUserSessionMap.get(uuid));
        return this.webSocketUserSessionMap.get(uuid);
    }

    public WebSocketUser remove(String uuid){
        return this.webSocketUserSessionMap.remove(uuid);
    }
}
