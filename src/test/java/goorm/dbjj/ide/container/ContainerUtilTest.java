package goorm.dbjj.ide.container;

import goorm.dbjj.ide.container.command.CommandStringBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DeregisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.StopTaskRequest;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class ContainerUtilTest {

    @Autowired
    private EcsClient ecsClient;

    @Autowired
    private ContainerUtil containerUtil;

    @Autowired
    private CommandStringBuilder commandStringBuilder;

    @Test
    @Disabled
    void createContainerImage() {

        String containerImage = containerUtil.createContainerImage(ProgrammingLanguage.PYTHON, "fsap-04fbb958d168856f3");

        assertThat(containerImage).isNotNull();

        //after
        ecsClient.deregisterTaskDefinition(DeregisterTaskDefinitionRequest.builder()
                .taskDefinition(containerImage)
                .build());
    }

    @Test
    @Disabled
    void deleteContainerImage() {
        String containerImage = "arn:aws:ecs:ap-northeast-2:092624380570:task-definition/inaws:12";
        containerUtil.deleteContainerImage(containerImage);

        assertThat(ecsClient.listTaskDefinitions().taskDefinitionArns()).doesNotContain(containerImage);
    }

    /**
     * 특정 프로젝트의 컨테이너를 수행시키는 테스트입니다.
     * 에러 발생 시 팀장에게 빠른 전달 부탁합니다. (과금과 관계 있음)
     */
    @Test
    @Disabled
    void runContainer() {

        String containerImageId = containerUtil.createContainerImage(ProgrammingLanguage.PYTHON, "fsap-04fbb958d168856f3");

        Assertions.assertDoesNotThrow(() -> {
            String taskArn = containerUtil.runContainer(containerImageId);

            StopTaskRequest stopTaskRequest = StopTaskRequest.builder()
                    .cluster("IDE_CONTAINER")
                    .task(taskArn)
                    .build();

            ecsClient.stopTask(stopTaskRequest);

            ecsClient.deregisterTaskDefinition(DeregisterTaskDefinitionRequest.builder()
                    .taskDefinition(containerImageId)
                    .build());
        });
    }

    @Test
    @Disabled
    void executeCommand() {

        //given
        String containerId = "arn:aws:ecs:ap-northeast-2:092624380570:task/IDE_CONTAINER/ae756009ce644ed6a3e3a00e136a2c1f";
        String command = "javac Main.java && java Main";
        String path = "/";
        String projectId = "ea90e9ad-3b48-459f-a460-15a762b2cb15";
        Long userId = 1L;
        String createdCommand = commandStringBuilder.createCommand(path, command, projectId, userId);
        //when
        containerUtil.executeCommand(containerId, createdCommand);
    }
}