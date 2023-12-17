package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.websocket.dto.UserInfoDto;
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

    /**
     * 실행중인 프로젝트인지 확인하는 로직
     * @return true:이미 존재함, false: 존재안함
     * */
    public boolean existsByProjectAndUser(UserInfoDto userInfoDto, String projectId) {
        return webSocketUserSessionMap.contains(new WebSocketUser(userInfoDto, projectId));
    }
}
