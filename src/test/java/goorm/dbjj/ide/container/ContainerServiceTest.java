package goorm.dbjj.ide.container;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.command.CommandStringBuilder;
import goorm.dbjj.ide.domain.project.model.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ContainerServiceTest {

    static class DummyContainerUtil implements ContainerUtil {

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

    private MemoryContainerRepository memoryContainerRepository = new MemoryContainerRepository();
    private ContainerService containerService = new ContainerService(
            new DummyContainerUtil(),
            memoryContainerRepository,
            new CommandStringBuilder(),
            new ExecutionIdMapper()
            );

    private Project createProject() {
        return Project.createProject(
                "name",
                "description",
                ProgrammingLanguage.PYTHON,
                "password"
        );
    }
    @AfterEach
    void afterEach() {
        memoryContainerRepository = new MemoryContainerRepository();
    }

    @Test
    void createProjectImage() {
        // given
        Project project = createProject();

        // when
        containerService.createProjectImage(project);

        // then
        assertEquals("containerImageId", project.getContainerImageId());
    }

    @Test
    void runContainer() {
        // given
        Project project = createProject();
        containerService.createProjectImage(project);

        // when
        containerService.runContainer(project);

        //then
        assertThat(memoryContainerRepository.find(project.getId())).isNotNull();
        assertThat(memoryContainerRepository.find(project.getId())).isEqualTo("containerId");
        assertThat(memoryContainerRepository.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("컨테이너 중지")
    void stopContainer() {
        // given
        Project project = createProject();
        containerService.createProjectImage(project);

        containerService.runContainer(project);

        // when
        containerService.stopContainer(project);

        // then
        assertThat(memoryContainerRepository.find(project.getId())).isNull();
        assertThat(memoryContainerRepository.size()).isEqualTo(0);
    }

    @Test
    void isRunning() {
        // given
        Project project = createProject();
        containerService.createProjectImage(project);
        containerService.runContainer(project);

        // when
        boolean isRunning1 = containerService.isContainerRunning(project);
        containerService.stopContainer(project);
        boolean isRunning2 = containerService.isContainerRunning(project);

        // then
        assertThat(isRunning1).isTrue();
        assertThat(isRunning2).isFalse();
    }

    @Test
    void deleteContainerImage() {
        // given
        Project project = createProject();
        containerService.createProjectImage(project);

        //Project에는 ContainerImage가 있어야 한다.
        assertThat(project.getContainerImageId()).isNotNull();

        // when
        containerService.deleteProjectImage(project);

        // then
        //삭제 후에는 Project에 ContainerImage가 없어야 한다.
        assertThat(project.getContainerImageId()).isNull();
    }

    @Test
    void deleteContainerImageEx1() {
        // given
        Project project = createProject();

        // when
        //Project에는 ContainerImage가 없다.
        assertThat(project.getContainerImageId()).isNull();

        // then
        //삭제 후에는 Project에 ContainerImage가 없어야 한다.
        assertThatThrownBy(() -> containerService.deleteProjectImage(project))
                .isInstanceOf(BaseException.class)
                .hasMessage("컨테이너 이미지가 존재하지 않습니다.");
    }
}