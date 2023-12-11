package goorm.dbjj.ide.container;

import goorm.dbjj.ide.domain.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DeregisterTaskDefinitionRequest;
import software.amazon.awssdk.services.ecs.model.StopTaskRequest;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ContainerUtilTest {

    @Autowired
    private EcsClient ecsClient;

    @Autowired
    private ContainerUtil containerUtil;

    @Test
    void createProjectImage() {

        Project project = new Project(
                UUID.randomUUID().toString(),
                "test",
                "test",
                null, // taskDefinition
                ProgrammingLanguage.PYTHON,
                "test",
                null
        );

        containerUtil.createProjectImage(project, "fsap-04fbb958d168856f3");

        assertThat(project.getTaskDefinition()).isNotNull();

        //after
        ecsClient.deregisterTaskDefinition(DeregisterTaskDefinitionRequest.builder()
                .taskDefinition(project.getTaskDefinition())
                .build());
    }

    /**
     * 특정 프로젝트의 컨테이너를 수행시키는 테스트입니다.
     * 에러 발생 시 팀장에게 빠른 전달 부탁합니다. (과금과 관계 있음)
     */
    @Test
    void runProjectContainer() {
        Project project = new Project(
                UUID.randomUUID().toString(),
                "test",
                "test",
                null, // taskDefinition
                ProgrammingLanguage.PYTHON,
                "test",
                null
        );

        containerUtil.createProjectImage(project, "fsap-04fbb958d168856f3");

        Assertions.assertDoesNotThrow(() -> {
            String taskArn = containerUtil.runProjectContainer(project);

            StopTaskRequest stopTaskRequest = StopTaskRequest.builder()
                    .cluster("IDE_CONTAINER")
                    .task(taskArn)
                    .build();

            ecsClient.stopTask(stopTaskRequest);

            ecsClient.deregisterTaskDefinition(DeregisterTaskDefinitionRequest.builder()
                    .taskDefinition(project.getTaskDefinition())
                    .build());
        });
    }
}