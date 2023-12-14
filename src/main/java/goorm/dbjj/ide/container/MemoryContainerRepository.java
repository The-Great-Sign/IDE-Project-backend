package goorm.dbjj.ide.container;

import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 수행중인 컨테이너와 프로젝트 이름을 매핑시켜 저장하는 메모리 저장소입니다.
 * 컨테이너 중복 실행 여부 등을 판단하기 위해 사용됩니다.
 */
@Repository
public class MemoryContainerRepository {

    /**
     * Map<ProjectId, ContainerId>
     */
    private final Map<String, String> containerMap = new ConcurrentHashMap<>();

    public void save(String projectId, String containerId) {
        containerMap.put(projectId, containerId);
    }

    /**
     * projectId를 통해 실행중인 컨테이너 ID를 획득합니다.
     * @param projectId
     * @return
     */
    public String find(String projectId) {
        return containerMap.get(projectId);
    }

    public String findProjectId(String containerId) {
        return containerMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(containerId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * 컨테이너가 종료되었을 때 호출하는 메서드입니다.
     * @param projectId
     */
    public void remove(String projectId) {
        containerMap.remove(projectId);
    }

    /**
     * 현재 수행중인 컨테이너의 개수를 반환합니다.
     * @return
     */
    public int size() {
        return containerMap.size();
    }
}
