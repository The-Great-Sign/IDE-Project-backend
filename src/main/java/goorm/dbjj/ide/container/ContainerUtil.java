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
    private final RunTaskRequestHelper runTaskRequestHelper;

    /**
     * Todo: 컨테이너에 명령어를 수행시키는 메서드
     */
    public void executeCommand(String projectId, String command) {
    }

    /**
     * 프로젝트 컨테이너 이미지를 생성하는 메서드
     *
     * 프로젝트의 생성과 동시에 컨테이너의 이미지를 생성합니다.
     * 수행 이후 Project.taskDefinition에 이미지의 식별자가 저장됩니다.
     *
     * @param project - 컨테이너 이미지를 생성할 프로젝트
     * @param accessPointId - EFS에 AccessPoint를 생성하고 그 ID를 파라미터로 전달받아야합니다.
     */
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

    /**
     * 프로젝트의 컨테이너를 실행시킵니다.
     *
     * 실행 설정 정보 생성 -> 실행 -> 컨테이너 ID 반환
     *
     * @param project
     * @return 컨테이너 식별자 - 추후 저장 필요
     */
    public String runProjectContainer(Project project) {
        log.trace("ContainerUtil.runProjectContainer called");

        RunTaskRequest runTaskRequest = runTaskRequestHelper.createRunTaskRequest(project.getTaskDefinition());
        RunTaskResponse runTaskResponse = ecsClient.runTask(runTaskRequest);

        log.debug("runTaskResponse: {}", runTaskResponse);
        return runTaskResponse.tasks().get(0).taskArn();
    }

    public void stopProjectContainer() {
    }
}
