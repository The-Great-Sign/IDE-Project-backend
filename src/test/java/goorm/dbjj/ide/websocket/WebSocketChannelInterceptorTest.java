package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.auth.jwt.JwtAuthProvider;
import goorm.dbjj.ide.auth.jwt.JwtIssuer;
import goorm.dbjj.ide.container.ProgrammingLanguage;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectUser;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.Role;
import goorm.dbjj.ide.domain.user.dto.SocialType;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class WebSocketChannelInterceptorTest {
    @Autowired
    private WebSocketUserSessionMapper webSocketUserSessionMapper;
    @Autowired
    private ProjectUserRepository projectUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WebSocketProjectUserCountMapper webSocketProjectUserCountMapper;

    // jwt
    @Autowired
    private JwtAuthProvider jwtAuthProvider;
    @Autowired
    private JwtIssuer jwtIssuer;
    private String TOKEN_PREFIX = "Bearer ";
    private String projectId;
    private String sessionId = UUID.randomUUID().toString();
    private WebSocketUser webSocketUser;

    @BeforeEach
    void TestSetting(){
        User user = userRepository.save(new User(1L, "email@email.com", "hello", "asdf", "asdf", Role.USER, SocialType.GOOGLE, "asdf", "asdf", LocalDateTime.now(), LocalDateTime.now()));
        Project project = projectRepository.save(Project.createProject("123","123",ProgrammingLanguage.PYTHON,"123",user));
        this.projectId = project.getId();
        projectUserRepository.save(new ProjectUser(project, user));
        this.webSocketUser = new WebSocketUser(new UserInfoDto(user), this.projectId);
        webSocketUserSessionMapper.clear();
        webSocketProjectUserCountMapper.clear();
    }

    @Test
    @DisplayName("CONNECT 전체 로직 테스트")
    void preSendConnect() {
        StompHeaderAccessor headerAccessor;
        User user = userRepository.findById(1L).orElseThrow(()-> new BaseException("테스트 에러"));
        UserInfoDto userInfoDto = new UserInfoDto(user);
        // URL 마지막 부분에 존재하는 프로젝트ID 가져오기
        // db 없이 테스트할때 주석 처리 해야함
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        // 프로젝트가 없을 경우
        if (projectOptional.isEmpty()) {
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 프로젝트에 참여한 유저가 아닐 경우
        if (!projectUserRepository.existsByProjectAndUser(projectOptional.get(), user)) {
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 동시접속 막는 로직 : 프로젝트가 참여한 유저인지 검증 로직
        webSocketUserSessionMapper.existsByProjectAndUser(userInfoDto, projectId);
        // 세션등록
        webSocketUserSessionMapper.put(this.sessionId, new WebSocketUser(userInfoDto, this.projectId));
        // 프로젝트 인원 수 증가
        webSocketProjectUserCountMapper.increaseCurrentUsersWithProjectId(this.projectId);

        assertEquals(webSocketProjectUserCountMapper.getCurrentUsersByProjectId(this.projectId), 1L);
    }

    @Test
    @DisplayName("CONNECT 동시 접속 실패 로직 테스트")
    void concurrentUserTesting() {
        StompHeaderAccessor headerAccessor;
        User user = userRepository.findById(1L).orElseThrow(()-> new BaseException("테스트 에러"));
        UserInfoDto userInfoDto = new UserInfoDto(user);
        // URL 마지막 부분에 존재하는 프로젝트ID 가져오기
        // db 없이 테스트할때 주석 처리 해야함
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        // 프로젝트가 없을 경우
        if (projectOptional.isEmpty()) {
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 프로젝트에 참여한 유저가 아닐 경우
        if (!projectUserRepository.existsByProjectAndUser(projectOptional.get(), user)) {
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 동시접속 테스트
        webSocketUserSessionMapper.put(this.sessionId, new WebSocketUser(userInfoDto, this.projectId));

        assertThrows(BaseException.class, () -> {
            // 동시접속 막는 로직 : 프로젝트가 참여한 유저인지 검증 로직
            webSocketUserSessionMapper.existsByProjectAndUser(userInfoDto, projectId);
        });
    }

    @Test
    @DisplayName("SUBSCRIBE 로직 테스트")
    void preSendSubscribe() {

        // Destination 주소 검증하기
        String subscribeProjectId = this.projectId;

        // 2. 사용자가 구독한 projectId 반환
        String projectId = this.projectId;

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if (!subscribeProjectId.equals(projectId)) {
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
        // 구독하기
        String subscribeType = "chat";

        // 이미 구독한 상태라면
        if (webSocketUser.isSubscribe(subscribeType)) {
            throw new BaseException("이미 구독한 채널 입니다.");
        }

        // 구독안했으면 구독해주기
        webSocketUser.startSubscribe(subscribeType);
    }

    @Test
    @DisplayName("SUBSCRIBE 동시 구독 실패 로직 테스트")
    void preSendFailSubscribe() {

        // Destination 주소 검증하기
        String subscribeProjectId = this.projectId;

        // 2. 사용자가 구독한 projectId 반환
        String projectId = this.projectId;

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if (!subscribeProjectId.equals(projectId)) {
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
        // 구독하기
        String subscribeType = "chat";

        // 이미 구독한 상태라면
        if (webSocketUser.isSubscribe(subscribeType)) {
            throw new BaseException("이미 구독한 채널 입니다.");
        }

        // 구독안했으면 구독해주기
        webSocketUser.startSubscribe(subscribeType);

        // 이미 구독한 상태라면
        assertThrows(BaseException.class, () -> {
                    if (webSocketUser.isSubscribe(subscribeType)) {
                        throw new BaseException("이미 구독한 채널 입니다.");
                    }
                });
    }

    @Test
    @DisplayName("SEND 성공 로직 테스트")
      void preSendSendTest(){
        User user = userRepository.findById(1L).orElseThrow(()-> new BaseException("테스트 에러"));
        UserInfoDto userInfoDto = new UserInfoDto(user);
        webSocketUserSessionMapper.put(this.sessionId, new WebSocketUser(userInfoDto, this.projectId));

        // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크
        String sessionId = this.sessionId;

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        if (webSocketUser == null) {
            throw new BaseException("잘못된 사용자 접근");
        }
        // 1. 사용자가 구독신청한 subscribeProjectId 반환
        String subscribeProjectId = this.projectId;

        // 2. 사용자가 구독한 projectId 반환
        String projectId = webSocketUser.getProjectId();

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if (!subscribeProjectId.equals(projectId)) {
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
    }

    @Test
    @DisplayName("SEND 실패 로직 테스트")
    void preSendSendFaileTest(){
        User user = userRepository.findById(1L).orElseThrow(()-> new BaseException("테스트 에러"));
        UserInfoDto userInfoDto = new UserInfoDto(user);
        webSocketUserSessionMapper.put(this.sessionId, new WebSocketUser(userInfoDto, this.projectId));

        // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크
        String sessionId = this.sessionId;

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        if (webSocketUser == null) {
            throw new BaseException("잘못된 사용자 접근");
        }
        // 1. 사용자가 구독신청한 subscribeProjectId 반환
        String subscribeProjectId = "2";

        // 2. 사용자가 구독한 projectId 반환
        String projectId = webSocketUser.getProjectId();

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if (!subscribeProjectId.equals(projectId)) {
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
    }
}