package goorm.dbjj.ide.container;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * executionId와 { projectId, userId }를 매핑하는 클래스입니다.
 */
@Component
public class ExecutionIdMapper {

    @Data
    public static class MappedInfo {
        private String projectId;
        private String userId;
    }
    private Map<String, MappedInfo> map = new ConcurrentHashMap<>();

    public void put(String executionId, String projectId, String userId) {
        MappedInfo mappedInfo = new MappedInfo();
        mappedInfo.setProjectId(projectId);
        mappedInfo.setUserId(userId);
        map.put(executionId, mappedInfo);
    }

    public MappedInfo get(String sessionId) {
        return map.get(sessionId);
    }

    public void remove(String sessionId) {
        map.remove(sessionId);
    }
}
