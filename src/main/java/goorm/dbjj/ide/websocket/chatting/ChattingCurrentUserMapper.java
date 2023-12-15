package goorm.dbjj.ide.websocket.chatting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 현재 접속한 채팅의 프로젝트의 유저 인원 파악
 * key : ProjectId
 * value : CurrentUsers
 * */
@Slf4j
@Component
public class ChattingCurrentUserMapper {
    private final Map<Long, Long> projectCurrentUserMapper;
    
    public ChattingCurrentUserMapper() {
        this.projectCurrentUserMapper = new ConcurrentHashMap<>();
    }

    /**
     * 입장 후 인원수 증가 로직
     * @param  projectId
     * */
    public void enterProjectCurrentUserMapper(Long projectId){
        projectCurrentUserMapper.compute(projectId, (key, value) -> (value == null) ? 1L : value + 1);
    }

    /**
     * 퇴장 후 인원수 감소 로직
     * @param  projectId
     * */
    public void exitProjectCurrentUserMapper(Long projectId){
        projectCurrentUserMapper.computeIfPresent(projectId, (key, value) -> (value > 1) ? --value : 0L);
    }

    /**
     * 현재 인원수 출력
     * @param  projectId
     * @return currentUsers
     * */
    public Long getProjectCurrentUserMapper(Long projectId){
        Long currentUsers = projectCurrentUserMapper.get(projectId);
        return currentUsers;
    }
}
