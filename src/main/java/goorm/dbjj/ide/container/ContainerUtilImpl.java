package goorm.dbjj.ide.container;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContainerUtilImpl implements ContainerUtil {

    private final EcsClient ecsClient;
    private final TaskDefinitionHelper taskDefinitionHelper;
    private final RunTaskRequestHelper runTaskRequestHelper;

    /**
     * Todo: 컨테이너에 명령어를 수행시키는 메서드
     */
    @Override
    public void executeCommand(String containerId, String command) {
        ExecuteCommandRequest request = ExecuteCommandRequest.builder()
                .cluster("IDE_CONTAINER")
                .task(containerId)
                .command(command)
                .interactive(true)
                .build();

        ExecuteCommandResponse response = ecsClient.executeCommand(request);
        log.debug("executeCommandResponse: {}", response);
    }

    @Override
    public String createContainerImage(ProgrammingLanguage programmingLanguage, String accessPointId) {
        log.trace("ContainerUtil.createProjectImage called");

        RegisterTaskDefinitionRequest request = taskDefinitionHelper.createRegisterTaskDefinitionRequest(
                programmingLanguage,
                accessPointId
        );

        RegisterTaskDefinitionResponse taskDefResponse = ecsClient.registerTaskDefinition(request);
        log.debug("taskDefResponse: {}", taskDefResponse);

        return taskDefResponse.taskDefinition().taskDefinitionArn();
    }

    @Override
    public String runContainer(String containerImageId) {
        log.trace("ContainerUtil.runProjectContainer called");

        RunTaskRequest runTaskRequest = runTaskRequestHelper.createRunTaskRequest(containerImageId);
        RunTaskResponse runTaskResponse = ecsClient.runTask(runTaskRequest);

        log.debug("runTaskResponse: {}", runTaskResponse);
        return runTaskResponse.tasks().get(0).taskArn();
    }

    @Override
    public void stopContainer(String containerId) {
        log.trace("ContainerUtil.stopProjectContainer called");

        StopTaskRequest stopTaskRequest = StopTaskRequest.builder()
                .cluster("IDE_CONTAINER")
                .task(containerId)
                .build();

        ecsClient.stopTask(stopTaskRequest);
    }
}
