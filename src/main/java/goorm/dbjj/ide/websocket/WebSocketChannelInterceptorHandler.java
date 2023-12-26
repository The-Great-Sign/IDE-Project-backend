package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import goorm.dbjj.ide.websocket.jwt.WebsocketJwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptorHandler {

    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final WebSocketProjectUserCountMapper webSocketProjectUserCountMapper;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final WebsocketJwt websocketJwt;

    /**
     * STOMP CONNECT 인터셉터 핸들러 수행
     * */
    public void stompConnect(
            String jwtToken,
            String projectId,
            String sessionId
    ) {
        User user = websocketJwt.getUserFromValidJwt(jwtToken);
        UserInfoDto userInfoDto = new UserInfoDto(user);
        log.trace("웹소켓 사용자 ID, 이름 가져오기 : {} {}", userInfoDto.getId(), userInfoDto.getNickname());

        // URL 마지막 부분에 존재하는 프로젝트ID 가져오기

        log.trace("웹소켓 [프로젝트] ID = {}", projectId);

        checkUserAccessToProject(projectId, user);

/*            // 동시접속 막는 로직 : 프로젝트가 참여한 유저인지 검증 로직
            webSocketUserSessionMapper.existsByProjectAndUser(userInfoDto, projectId);*/

        // 세션등록
        webSocketUserSessionMapper.put(sessionId, new WebSocketUser(userInfoDto, projectId));

        // 프로젝트 인원 수 증가
        webSocketProjectUserCountMapper.increaseCurrentUsersWithProjectId(projectId);
    }

    /**
     * STOMP SUBSCRIBE 인터셉터 핸들러 수행
     * */
    public void stompSubscribe(
            WebSocketUser webSocketUser,
            String subscribeProjectId,
            String subscribeType
    ) {
        log.trace("웹소켓 [SUBSCRIBE] 요청");
        // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크

        checkEqualsSubscribeDestinationByProjectId(webSocketUser, subscribeProjectId);
        checkExistsBySubscribeType(webSocketUser, subscribeType);

        // 구독안했으면 구독해주기
        webSocketUser.startSubscribe(subscribeType);
    }

    /**
     * STOMP SEND 인터셉터 핸들러 수행
     * */
    public void stompSend(
            WebSocketUser webSocketUser,
            String subscribeProjectId,
            String subscribeType
    ) {
        log.trace("웹소켓 [SUBSCRIBE] 요청");
        // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크

        // Destination 주소 검증하기
        checkEqualsSubscribeDestinationByProjectId(webSocketUser, subscribeProjectId);
        checkExistsBySubscribeType(webSocketUser, subscribeType);
    }

    /**
     * STOMP SUBSCRIBE 시 동일한 구독을 방지하는 로직
     * */
    private void checkExistsBySubscribeType(
            WebSocketUser webSocketUser,
            String subscribeType
    ) {
        // 구독하기
        // /user로 시작하는 경우 chatuser
        log.trace("웹소켓 [SubscribeType] = {}", subscribeType);

        // 이미 구독한 상태라면
        if (webSocketUser.isSubscribe(subscribeType)) {
            log.warn("웹소켓 구독 중복 에러 입니다. subscribeType = {}", subscribeType);
            throw new BaseException("이미 구독한 채널 입니다.");
        }
    }

    /**
     * STOMP SUBSCRIBE 시 다른 프로젝트 ID의 구독을 방지하는 로직
     * */
    private void checkEqualsSubscribeDestinationByProjectId(
            WebSocketUser webSocketUser,
            String subscribeProjectId
    ) {
        // Destination 주소 검증하기
        log.trace("웹소켓 Destination 주소가 같은지 판별");
        // 1. 사용자가 구독신청한 subscribeProjectId 반환

        // 2. 사용자가 구독한 projectId 반환
        String projectId = webSocketUser.getProjectId();
        log.trace("웹소켓 Destination 주소 : [현재 주소] proejct Id = {} , [구독 주소] proejct Id = {}", projectId, subscribeProjectId);

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if (!subscribeProjectId.equals(projectId)) {
            log.warn("웹소켓 접속한 프로젝트와 구독하고자하는 프로젝트가 다릅니다");
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
    }


    /**
     * 사용자가 프로젝트에 접근할 권한이 있는지 확인하는 로직
     */
    private void checkUserAccessToProject(
            String projectId,
            User user
    ) {
        // db 없이 테스트할때 주석 처리 해야함
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        // 프로젝트가 없을 경우
        if (projectOptional.isEmpty()) {
            log.error("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }

        // 프로젝트에 참여한 유저가 아닐 경우
        if (!projectUserRepository.existsByProjectAndUser(projectOptional.get(), user)) {
            log.error("웹소켓 해당 프로젝트의 소유권 없는자가 접근했습니다.");
            throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
        }
    }

    /**
     * jwt 없이 User정보 받기!
     * */
    /*
    private User getUserFromValidUserId(StompHeaderAccessor headerAccessor) {
        List<String> userIdNativeHeader = headerAccessor.getNativeHeader("UserId");

        if(userIdNativeHeader.isEmpty()){
            log.error("웹소켓에 연결될 유저가 존재하지 않습니다.");
            throw new BaseException("웹소켓에 연결될 유저가 존재하지 않습니다.");
        }

        Long userId = Long.valueOf(userIdNativeHeader.get(0)); // jwt accesstoken "Bearer "
        log.trace("웹소켓 userId = {}", userId);

        return userRepository.findById(userId).orElseThrow(() -> new BaseException("프로젝트 아이디가 없습니다"));
    }*/
}
