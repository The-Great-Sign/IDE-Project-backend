package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.websocket.dto.UserInfoDto;
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
    private final UserInfoDto userInfoDto;
    private final String projectId;
    private final Set<String> subscribes;

    /**
     * 생성자
     * */
    public WebSocketUser(UserInfoDto userInfoDto, String projectId) {
        this.userInfoDto = userInfoDto;
        this.projectId = projectId;
        this.subscribes = ConcurrentHashMap.newKeySet(); // 키를 이용한 Set 구현.
    }

    /**
     * 구독타입 저장하기
     * */
    public void startSubscribe(String subscribeType){
        this.subscribes.add(subscribeType);
    }

    /**
     * 구독하고 있는지 확인하는 메서드
     * */
    public boolean isSubscribe(String subscribeType){
        // 구독하고 있다면 true, 안하면 false
        return this.subscribes.stream().anyMatch(s -> s.equals(subscribeType));
    }

    /**
     * userId와 projectId 두개가 동일한 객체가 존재하는지 비교문
     * */
    public boolean isSameWith(Long userId, String projectId){
        return this.userInfoDto.getId().equals(userId) && this.projectId.equals(projectId);
    }
}
