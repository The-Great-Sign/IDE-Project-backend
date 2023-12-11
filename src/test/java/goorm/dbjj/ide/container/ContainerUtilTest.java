package goorm.dbjj.ide.container;
import goorm.dbjj.ide.domain.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DeregisterTaskDefinitionRequest;
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

        containerUtil.createProjectImage(project,"fsap-04fbb958d168856f3");

        assertThat(project.getTaskDefinition()).isNotNull();

        //after
        ecsClient.deregisterTaskDefinition(DeregisterTaskDefinitionRequest.builder()
                .taskDefinition(project.getTaskDefinition())
                .build());

    }
}