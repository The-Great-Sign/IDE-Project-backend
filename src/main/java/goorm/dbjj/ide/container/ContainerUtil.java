package goorm.dbjj.ide.container;

import goorm.dbjj.ide.domain.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContainerUtil {

    private final EcsClient ecsClient;
    private final TaskDefinitionHelper taskDefinitionHelper;

    /**
     * Todo: 컨테이너에 명령어를 수행시키는 메서드
     */
    public void executeCommand(String projectId, String command) {
    }

    public void createProjectImage(Project project, String accessPointId) {
        log.trace("ContainerUtil.createProjectImage called");

        RegisterTaskDefinitionRequest request = taskDefinitionHelper.createRegisterTaskDefinitionRequest(
                project.getProgrammingLanguage(),
                accessPointId
        );

        RegisterTaskDefinitionResponse taskDefResponse = ecsClient.registerTaskDefinition(request);
        log.debug("taskDefResponse: {}", taskDefResponse);

        project.setTaskDefinition(taskDefResponse.taskDefinition().taskDefinitionArn());
    }


    public void runProjectContainer(Project project) {
    }

    public void stopProjectContainer() {
    }
}
