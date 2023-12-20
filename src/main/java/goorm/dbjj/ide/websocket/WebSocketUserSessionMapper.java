package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
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
    public void existsByProjectAndUser(UserInfoDto userInfoDto, String projectId) {
        if(webSocketUserSessionMap.values().stream()
                .anyMatch(w ->  w.getProjectId().equals(projectId) && w.getUserInfoDto().getId().equals(userInfoDto.getId()))){
            log.warn("이미 실행중인 프로젝트 입니다.");
            throw new BaseException("이미 실행중인 프로젝트 입니다!");
        }
    }

    /**
     * userInfoDto와 projectId를 이용해서 세션키를 찾는 로직
     * @return sessionId
     * */
    public String findSessionIdByProjectAndUserInfoDto(Long userId, String projectId) {
        return webSocketUserSessionMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isSameWith(userId, projectId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
