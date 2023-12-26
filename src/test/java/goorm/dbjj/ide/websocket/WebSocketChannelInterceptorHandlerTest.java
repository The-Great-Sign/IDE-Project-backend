package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.ProgrammingLanguage;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectUser;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.Role;
import goorm.dbjj.ide.domain.user.dto.SocialType;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class WebSocketChannelInterceptorHandlerTest {
    @Autowired
    private WebSocketChannelInterceptorHandler webSocketChannelInterceptorHandler;
    @Autowired
    private ProjectUserRepository projectUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WebSocketUserSessionMapper webSocketUserSessionMapper;
    @Autowired
    private WebSocketProjectUserCountMapper webSocketProjectUserCountMapper;

    @Value("${jwt.secretKey}")
    String secretKey;

    private String projectId1;
    private String projectId2;

    private final String email1 = "test1@google.com";
    private final String token1 = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MUBnb29nbGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImV4cCI6MTcwNDc4Njc0Mn0.U-wMKE9BgXoA9EHrId0JXlAxLf-K3-EKHYW_lRxFybU";

    private final String email2 = "test2@google.com";
    private final String token2 = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MkBnb29nbGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImV4cCI6MTcwNDAyNTAyMX0.dt00OfuHdFnr1X-y7itvgRNhN5N0RSci-V2D1K0JtfM";



    private final String sessionId1 = "1";
    private final String sessionId2 = "2";

    private WebSocketUser webSocketUserTest1;

    @BeforeEach
    void TestSetting() {
        User user1 = userRepository.save(
                new User(
                        1L,
                        this.email1,
                        "test1@google.com",
                        "imageUrl",
                        "password",
                        Role.USER,
                        SocialType.GOOGLE,
                        "accessToken",
                        "refreshToken",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));

        User user2 = userRepository.save(
                new User(
                        2L,
                        this.email2,
                        "test1@google.com",
                        "imageUrl",
                        "password",
                        Role.USER,
                        SocialType.GOOGLE,
                        "accessToken",
                        "refreshToken",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ));



        Project project1 = projectRepository.save(
                Project.createProject(
                        "1",
                        "1",
                        ProgrammingLanguage.PYTHON,
                        "1",
                        user1)
        );

        Project project2 = projectRepository.save(
                Project.createProject(
                        "1",
                        "1",
                        ProgrammingLanguage.PYTHON,
                        "1",
                        user1)
        );

        projectUserRepository.save(
                new ProjectUser(
                        project1,
                        user1
                ));

        projectUserRepository.save(
                new ProjectUser(
                        project1,
                        user2
                ));

        projectUserRepository.save(
                new ProjectUser(
                        project2,
                        user1
                ));

        this.projectId1 = project1.getId();
        this.projectId2 = project2.getId();

    }
    @Test
    @DisplayName("CONNECT 성공 로직 테스트1")
    void stompConnect() {
        //given
        log.info("projectId = {}", this.projectId1);
        webSocketChannelInterceptorHandler.stompConnect(
                this.token1,
                this.projectId1,
                this.sessionId1);
        webSocketChannelInterceptorHandler.stompConnect(
                this.token1,
                this.projectId2,
                this.sessionId2);

        //when
        Long currentUsersByProjectId1 = webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId1);
        Long currentUsersByProjectId2 = webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId2);

        //then
        assertEquals(currentUsersByProjectId1, 1L);
        assertEquals(currentUsersByProjectId2, 1L);
    }

    @Test
    @DisplayName("CONNECT 성공 로직 테스트2")
    void stompConnect2() {
        //given
        log.info("projectId = {}", this.projectId1);
        webSocketChannelInterceptorHandler.stompConnect(
                this.token1,
                this.projectId1,
                this.sessionId1);
        webSocketChannelInterceptorHandler.stompConnect(
                this.token2,
                this.projectId1,
                this.sessionId2);

        //when
        Long currentUsersByProjectId1 = webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId1);

        //then
        assertEquals(currentUsersByProjectId1, 2L);
    }

    @Test
    @Disabled
    @DisplayName("CONNECT 동시접속 실패 로직 테스트")
    void stompFailConnect() {
        //given then?
        log.info("projectId = {}", this.projectId1);
        webSocketChannelInterceptorHandler.stompConnect(
                this.token1,
                this.projectId1,
                this.sessionId1);

        // 동시 접속 로직 테스트
        assertThrows(BaseException.class,()->{
            webSocketChannelInterceptorHandler.stompConnect(
                    this.token1,
                    this.projectId1,
                    this.sessionId1);
        });
    }

    @Test
    @DisplayName("SUBSCRIBE 다중 구독 성공 로직")
    void stompSubscribe() {
        webSocketChannelInterceptorHandler.stompConnect(
                this.token1,
                this.projectId1,
                sessionId1);

        this.webSocketUserTest1 = webSocketUserSessionMapper.get(sessionId1);


        webSocketChannelInterceptorHandler.stompSubscribe(
                this.webSocketUserTest1,
                this.projectId1,
                "chat"
        );

        webSocketChannelInterceptorHandler.stompSubscribe(
                this.webSocketUserTest1,
                this.projectId1,
                "terminal"
        );

        webSocketChannelInterceptorHandler.stompSubscribe(
                this.webSocketUserTest1,
                this.projectId1,
                "file"
        );

        webSocketChannelInterceptorHandler.stompSubscribe(
                this.webSocketUserTest1,
                this.projectId1,
                "container-loading"
        );

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(this.sessionId1);

        //when
        Long currentUsersByProjectId1 = webSocketProjectUserCountMapper.getCurrentUsersByProjectId(projectId1);

        //then
        assertEquals(currentUsersByProjectId1, 1L);
        assertTrue(webSocketUser.isSubscribe("chat"));
        assertTrue(webSocketUser.isSubscribe("terminal"));
        assertTrue(webSocketUser.isSubscribe("file"));
        assertTrue(webSocketUser.isSubscribe("container-loading"));
    }
}