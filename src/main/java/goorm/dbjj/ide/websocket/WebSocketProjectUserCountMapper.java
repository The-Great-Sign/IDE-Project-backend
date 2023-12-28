package goorm.dbjj.ide.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 현재 접속한 채팅의 프로젝트의 유저 인원 파악
 * key : ProjectId
 * value : CurrentUsers
 * */
@Slf4j
@Component
public class WebSocketProjectUserCountMapper {
    private final ConcurrentHashMap<String, Long> projectCurrentUserMapper;
    
    public WebSocketProjectUserCountMapper() {
        this.projectCurrentUserMapper = new ConcurrentHashMap<>();
    }

    /**
     * 입장 후 인원수 증가 로직
     * @param  projectId
     * */
    public void increaseCurrentUsersWithProjectId(String projectId){
        projectCurrentUserMapper.compute(projectId, (key, value) -> (value == null) ? 1L : value + 1);
        log.trace("웹소켓 현재 [프로젝트ID] = {} [인원] = {}", projectId, projectCurrentUserMapper.get(projectId));
    }

    /**
     * 퇴장 후 인원수 감소 로직
     * @param  projectId
     * */
    public void decreaseCurrentUsersWithProjectId(String projectId){
        projectCurrentUserMapper.computeIfPresent(projectId, (key, value) -> (value > 1) ? --value : 0L);
        log.trace("웹소켓 현재 [projectId] = {} [인원] = {}", projectId, projectCurrentUserMapper.get(projectId));
    }

    /**
     * 현재 인원수 출력
     * @param  projectId
     * @return currentUsers
     * */
    public Long getCurrentUsersByProjectId(String projectId){
        return projectCurrentUserMapper.get(projectId);
    }

    /**
     * 프로젝트가 0명일 경우 Mapper에서 삭제
     * @param projectId
     * */
    public void removeCurrentUsersByProjectId(String projectId){
        projectCurrentUserMapper.remove(projectId);
    }

    /**
     * 테스트용 코드
     * */
    public void clear(){
        projectCurrentUserMapper.clear();
    }
}
