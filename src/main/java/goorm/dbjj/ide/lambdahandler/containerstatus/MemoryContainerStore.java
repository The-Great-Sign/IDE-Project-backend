package goorm.dbjj.ide.lambdahandler.containerstatus;

import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 수행중인 컨테이너와 프로젝트 이름을 매핑시켜 저장하는 메모리 저장소입니다.
 * 컨테이너 중복 실행 여부 등을 판단하기 위해 사용됩니다.
 */
@Component
@Slf4j
public class MemoryContainerStore implements ContainerStore {

    /**
     * Map<ProjectId, ContainerInfo>
     */
    private final Map<String, ContainerInfo> containerMap = new ConcurrentHashMap<>();

    @Override
    public void save(String projectId, String containerId) {
        containerMap.put(projectId, new ContainerInfo(containerId));
    }

    /**
     * projectId를 통해 실행중인 컨테이너 정보를 획득합니다.
     * @param projectId
     * @return
     */
    @Override
    public ContainerInfo find(String projectId) {
        return containerMap.get(projectId);
    }

    /**
     * Project ID를 반환합니다.
     * @param containerId
     * @return
     */
    @Override
    public String findProjectId(String containerId) {
        return containerMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().getContainerId().equals(containerId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * 컨테이너가 종료되었을 때 호출하는 메서드입니다.
     * @param projectId
     */
    @Override
    public void remove(String projectId) {
        containerMap.remove(projectId);
    }

    /**
     * 현재 수행중인 컨테이너의 개수를 반환합니다.
     * @return
     */
    @Override
    public int size() {
        return containerMap.size();
    }

    @Override
    public boolean mark(String projectId) {
        if(containerMap.get(projectId) == null) {
            containerMap.put(projectId, new ContainerInfo(null));
            log.debug("컨테이너 마킹 : {}", projectId);
            return true;
        } else {
            log.debug("이미 마킹되어있습니다.");
            return false;
        }
    }
}
