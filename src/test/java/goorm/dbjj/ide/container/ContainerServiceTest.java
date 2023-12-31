package goorm.dbjj.ide.container;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.command.CommandStringBuilder;
import goorm.dbjj.ide.lambdahandler.containerstatus.ContainerStore;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.Role;
import goorm.dbjj.ide.domain.user.dto.SocialType;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.lambdahandler.containerstatus.MemoryContainerStore;
import goorm.dbjj.ide.mock.DummyContainerUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ContainerServiceTest {
    private ContainerStore containerStore = new MemoryContainerStore();
    private ContainerService containerService = new ContainerServiceImpl(
            new DummyContainerUtil(),
            containerStore,
            new CommandStringBuilder()
    );

    private Project createProject() {
        return Project.createProject(
                "name",
                "description",
                ProgrammingLanguage.PYTHON,
                "password",
                new User(1L,
                        "email",
                        "nickname",
                        "imageUrl",
                        "password",
                        Role.USER,
                        SocialType.GOOGLE,
                        "socialId",
                        "refreshToken",
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
    }

    @AfterEach
    void afterEach() {
        containerStore = new MemoryContainerStore();
    }

    @Test
    void createProjectImage() {
        // given
        Project project = createProject();

        // when
        String containerImageId = containerService.createProjectImage(project);

        // then
        assertEquals("containerImageId", containerImageId);
    }

    @Test
    void runContainer() {
        // given
        Project project = createProject();
        String containerImageId = containerService.createProjectImage(project);
        project.setContainerImageId(containerImageId);

        // when
        containerService.runContainer(project);

        //then
        assertThat(containerStore.find(project.getId())).isNotNull();
        assertThat(containerStore.find(project.getId()).getContainerId()).isEqualTo("containerId");
        assertThat(containerStore.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("컨테이너 중지")
    void stopContainer() {
        // given
        Project project = createProject();
        String containerImageId = containerService.createProjectImage(project);
        project.setContainerImageId(containerImageId);

        containerService.runContainer(project);

        // when
        containerService.stopContainer(project);

        // then
        assertThat(containerStore.find(project.getId())).isNull();
        assertThat(containerStore.size()).isEqualTo(0);
    }

    @Test
    void isRunning() {
        // given
        Project project = createProject();
        String containerImageId = containerService.createProjectImage(project);
        project.setContainerImageId(containerImageId);

        containerService.runContainer(project);

        // when

        //컨테이너를 시작한 즉시에는 PENDING 상태로, 실행중이 아니다.
        boolean isRunning1 = containerService.isContainerRunning(project);

        // RUNNING 상태가 되면 실행중 상태로 바꾼다. 이 이벤트는 외부에서 전송됨
        containerStore.find(project.getId()).setRunning();

        // RUNNING 상태에는 isContainerRunning이 true를 반환한다.
        boolean isRunning2 = containerService.isContainerRunning(project);

        containerService.stopContainer(project);

        // 컨테이너를 종료한 즉시 메모리에서 삭제되며 isRunning은 false를 반환한다.
        boolean isRunning3 = containerService.isContainerRunning(project);

        // then
        assertThat(isRunning1).isFalse();
        assertThat(isRunning2).isTrue();
        assertThat(isRunning3).isFalse();
    }

    @Test
    void executeSuccess() {
        Project project = createProject();
        containerStore.save(project.getId(), "containerId");

        //Running 상태로 변경
        containerStore.find(project.getId()).setRunning();

        containerService.executeCommand(project, "/app", "python hello.py", 1L);
    }

    @Test
    void executeFailNotRunning() {
        Project project = createProject();

        //Running 상태로 변경
        assertThatThrownBy(
                () -> containerService.executeCommand(project, "/app", "python hello.py", 1L)
        ).isInstanceOf(BaseException.class).hasMessage("컨테이너가 실행중이지 않습니다.");
    }

    @Test
    void executeFailPending() {
        Project project = createProject();
        containerService.createProjectImage(project);
        containerStore.save(project.getId(), "containerId");

        //Pending 상태로 변경
        containerStore.find(project.getId()).setPending();

        assertThatThrownBy(
                () -> containerService.executeCommand(project, "/app", "python hello.py", 1L)
        ).isInstanceOf(BaseException.class).hasMessage("컨테이너가 실행중이지 않습니다.");

    }

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