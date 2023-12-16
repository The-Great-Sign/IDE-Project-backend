package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 웹소켓 세션에 포함될 내용
 * */
@Slf4j
@Getter
public class WebSocketUser {
    private final UserInfoDto userInfoDto;
    private final String projectId;
    private final Set<String> subscribes;

    public WebSocketUser(UserInfoDto userInfoDto, String projectId) {
        this.userInfoDto = userInfoDto;
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

    /**
    * 이미 실행중인 프로젝트인지 비교할때 사용
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketUser that = (WebSocketUser) o;
        return Objects.equals(userInfoDto, that.userInfoDto) && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userInfoDto, projectId);
    }
}
