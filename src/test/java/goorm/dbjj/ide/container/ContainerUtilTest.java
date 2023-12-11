package goorm.dbjj.ide.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DeregisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.StopTaskRequest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ContainerUtilTest {

    @Autowired
    private EcsClient ecsClient;

    @Autowired
    private ContainerUtil containerUtil;

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
}