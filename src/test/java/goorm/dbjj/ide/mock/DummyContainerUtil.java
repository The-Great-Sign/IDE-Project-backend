package goorm.dbjj.ide.mock;

import goorm.dbjj.ide.container.ContainerUtil;
import goorm.dbjj.ide.container.ProgrammingLanguage;

public class DummyContainerUtil implements ContainerUtil {


    @Override
    public String executeCommand(String containerId, String command) {
        return "sessionId";
    }

    @Override
    public String createContainerImage(ProgrammingLanguage programmingLanguage, String accessPointId) {
        return "containerImageId";
    }

    @Override
    public void deleteContainerImage(String containerImageId) {
    }

    @Override
    public String runContainer(String containerImageId) {
        return "containerId";
    }

    @Override
    public void stopContainer(String containerId) {
    }
}
