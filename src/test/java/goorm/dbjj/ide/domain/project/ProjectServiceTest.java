package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.container.ProgrammingLanguage;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.Role;
import goorm.dbjj.ide.domain.user.dto.SocialType;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.efs.EfsAccessPointUtil;
import goorm.dbjj.ide.mock.DummyContainerService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProjectService를 Spring Context에서 테스트하기 위한 클래스
 * ContainerService만 모킹하여 사용합니다. (AWS SDK 사용하기 때문)
 */
@SpringBootTest
@Import(ProjectServiceTest.Config.class)
@Transactional
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContainerService containerService;

    @Autowired
    ProjectUserRepository projectUserRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        DummyContainerService dummyContainerService= (DummyContainerService) containerService;
        dummyContainerService.clearMethodCallCount();
    }

    private User createUser() {
        return new User(
                1L,
                "name",
                "email",
                "imageUrl",
                "password",
                Role.USER,
                SocialType.GOOGLE,
                "accessToken",
                "refreshToken",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @TestConfiguration
    static class Config {
        @Bean
        public ContainerService containerService() {
            return new DummyContainerService();
        }

        @Bean
        public EfsAccessPointUtil efsAccessPointUtil() {
            return new EfsAccessPointUtil() {
                @Override
                public String generateAccessPoint(String projectId) {
                    return "/app/" + projectId;
                }

                @Override
                public void deleteAccessPoint(String accessPointId) {

                }
            };

        }
    }

    @Test
    void createProjectSuccess() {
        // given
        User user = createUser();
        userRepository.save(user);

        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "name",
                "description",
                ProgrammingLanguage.PYTHON,
                "password"
                );

        // when
        ProjectDto projectDto = projectService.createProject(requestDto, user);

        em.flush();
        em.clear();

        // then
        User creator = userRepository.findById(user.getId()).get();
        Project project = projectRepository.findById(projectDto.getId()).get();

        //DB에 프로젝트 정보가 잘 저장되어있어야 한다.
        assertThat(project.getName()).isEqualTo(requestDto.getName());
        assertThat(project.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(project.getProgrammingLanguage()).isEqualTo(requestDto.getProgrammingLanguage());
        assertThat(passwordEncoder.matches(requestDto.getPassword(), project.getPassword())).isTrue();
        assertThat(project.getCreator()).isEqualTo(creator);

        // 프로젝트 생성과 함께 creator가 프로젝트에 참여하도록 함
        assertThat(projectUserRepository.existsByProjectAndUser(project, creator)).isTrue();
        assertThat(projectUserRepository.count()).isEqualTo(1);

        // 프로젝트 생성 시점에 컨테이너 이미지 생성
        assertThat(project.getContainerImageId()).isEqualTo("containerImageId");
        assertThat(project.getAccessPointId()).isEqualTo("/app/" + project.getId());
    }


    /**
     * 프로젝트를 삭제하는 테스트입니다.
     * 원래는 프로젝트 생성과 독립적으로 수행되어야 하는 것이 맞지만, 편의상 프로젝트 생성과 함께 수행합니다.
     */
    @Test
    void deleteProjectSuccess() {
        // given
        User user = createUser();
        userRepository.save(user);

        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "name",
                "description",
                ProgrammingLanguage.PYTHON,
                "password"
                );

        ProjectDto projectDto = projectService.createProject(requestDto, user);

        em.flush();
        em.clear();

        // when
        projectService.deleteProject(projectDto.getId(), user);

        em.flush();
        em.clear();

        // then
        DummyContainerService dummyContainerService= (DummyContainerService) containerService;

        assertThat(projectRepository.count()).isEqualTo(0);
        assertThat(projectUserRepository.count()).isEqualTo(0);
        assertThat(dummyContainerService.getMethodCallCount("deleteProjectImage")).isEqualTo(1);
        assertThat(dummyContainerService.getMethodCallCount("stopContainer")).isEqualTo(1);
    }
}