package goorm.dbjj.ide.container;

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
    private final RunTaskRequestHelper runTaskRequestHelper;

    /**
     * Todo: 컨테이너에 명령어를 수행시키는 메서드
     */
    public void executeCommand(String containerId, String command) {
    }

    /**
     * 컨테이너 이미지를 생성하는 메서드
     *
     * 프로젝트의 생성과 동시에 컨테이너의 이미지를 생성합니다.
     * 수행 이후 Project.taskDefinition에 이미지의 식별자가 저장됩니다.
     *
     * @param programmingLanguage - 컨테이너 이미지를 생성할 언어
     * @param accessPointId - EFS에 AccessPoint를 생성하고 그 ID를 파라미터로 전달받아야합니다.
     * @return 생성된 컨테이너 이미지의 식별자
     */
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

    /**
     * 프로젝트의 컨테이너를 실행시킵니다.
     *
     * 실행 설정 정보 생성 -> 실행 -> 컨테이너 ID 반환
     *
     * @param containerImageId - 컨테이너 이미지 식별자 (TaskDefinition) - Project 레벨에서 보유
     * @return 컨테이너 식별자 - 추후 저장 필요
     */
    public String runContainer(String containerImageId) {
        log.trace("ContainerUtil.runProjectContainer called");

        RunTaskRequest runTaskRequest = runTaskRequestHelper.createRunTaskRequest(containerImageId);
        RunTaskResponse runTaskResponse = ecsClient.runTask(runTaskRequest);

        log.debug("runTaskResponse: {}", runTaskResponse);
        return runTaskResponse.tasks().get(0).taskArn();
    }

    /**
     * 컨테이너를 중지합니다.
     * 외부에서 별도로 유지하고 있던 ContainerId를 넘겨줘야합니다.
     */
    public void stopContainer(String containerId) {
        log.trace("ContainerUtil.stopProjectContainer called");

        StopTaskRequest stopTaskRequest = StopTaskRequest.builder()
                .cluster("IDE_CONTAINER")
                .task(containerId)
                .build();

        ecsClient.stopTask(stopTaskRequest);
    }
}
