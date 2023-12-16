package goorm.dbjj.ide.container;

import goorm.dbjj.ide.domain.project.model.Project;

public interface ContainerService {
    void executeCommand(Project project, String path, String command, String userId);

    String createProjectImage(Project project);

    void deleteProjectImage(Project project);

    void runContainer(Project project);

    void stopContainer(Project project);

    boolean isContainerRunning(Project project);
}
