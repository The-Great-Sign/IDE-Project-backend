package goorm.dbjj.ide.mock;

import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.domain.project.model.Project;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DummyContainerService implements ContainerService {

    private Map<String, Long> methodCallCountMap = new HashMap<>();

    @Override
    public void executeCommand(Project project, String path, String command, Long userId) {
    }

    @Override
    public String createProjectImage(Project project) {
        return "containerImageId";
    }

    @Override
    public void deleteProjectImage(Project project) {
        methodCallCountMap.put("deleteProjectImage", methodCallCountMap.getOrDefault("deleteProjectImage", 0L) + 1);
    }

    @Override
    public void runContainer(Project project) {

    }

    @Override
    public void stopContainer(Project project) {
        methodCallCountMap.put("stopContainer", methodCallCountMap.getOrDefault("stopContainer", 0L) + 1);
    }

    @Override
    public boolean isContainerRunning(Project project) {
        return false;
    }

    public long getMethodCallCount(String methodName) {
        return methodCallCountMap.getOrDefault(methodName, 0L);
    }

    public void clearMethodCallCount() {
        methodCallCountMap.clear();
    }
}
