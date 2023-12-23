package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.auth.jwt.JwtAuthProvider;
import goorm.dbjj.ide.auth.jwt.JwtIssuer;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP 로 클라이언트에게 전달받는 모든 명령어들이 인증된 사용자, 프로젝트인지 확인하는 로직
 * 구독시 WebSocketUserMapper에 Set 자료형에 구독한 채팅방 이름을 저장하고 이중 접속을 막는 로직이 담겨 있습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;
    private final WebSocketProjectUserCountMapper webSocketProjectUserCountMapper;

    // jwt
    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;
    private final UserRepository userRepository;
    private final String TOKEN_PREFIX = "Bearer ";

    /**
     * 클라이언트가 보낸 STOMP 메세지 처리하기전 인터셉터
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            log.trace("웹소켓 [CONNECT] 요청");
            List<String> authorization = headerAccessor.getNativeHeader("Authorization");
            String accessToken = authorization.get(0); // jwt accesstoken "Bearer "
            log.trace("액세스 토큰 : {}", accessToken);

            if (accessToken == null || !accessToken.startsWith(TOKEN_PREFIX)) {
                log.debug("유효한 토큰 형식이 아닙니다. : {}", accessToken);
                throw new BaseException("유효한 토큰 형식이 아닙니다.");
            }

            // 토큰만 추출.
            accessToken = accessToken.substring(TOKEN_PREFIX.length());

            // 토큰이 유효한지 검증.
            if (!jwtAuthProvider.validateToken(accessToken)) {
                log.debug("토큰이 유효하지 않습니다. : {}", accessToken);
                throw new BaseException("토큰이 유효하지 않습니다.");
            }

            // 토큰을 사용해서 유저 정보 가져오기.
            String userEmail = jwtIssuer.getSubject(jwtIssuer.getClaims(accessToken));
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new BaseException("해당 이메일을 가진 유저가 없습니다."));

            UserInfoDto userInfoDto = new UserInfoDto(user);
            log.trace("웹소켓 사용자 ID, 이름 가져오기 : {} {}", userInfoDto.getId(), userInfoDto.getNickname());

            // URL 마지막 부분에 존재하는 프로젝트ID 가져오기
            List<String> projectIdValues = headerAccessor.getNativeHeader("ProjectId");
            String projectId = projectIdValues.get(0);

            log.trace("{}", projectId);

            // db 없이 테스트할때 주석 처리 해야함
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            // 프로젝트가 없을 경우
            if (projectOptional.isEmpty()) {
                log.error("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
                throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
            }

            // 프로젝트에 참여한 유저가 아닐 경우
            if (!projectUserRepository.existsByProjectAndUser(projectOptional.get(), user)) {
                log.error("해당 프로젝트의 소유권 없는자가 접근했습니다.");
                throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
            }

/*            // 동시접속 막는 로직 : 프로젝트가 참여한 유저인지 검증 로직
            webSocketUserSessionMapper.existsByProjectAndUser(userInfoDto, projectId);*/

            // 세션등록
            String sessionId = getSessionId(headerAccessor);
            webSocketUserSessionMapper.put(sessionId, new WebSocketUser(userInfoDto, projectId));

            // 프로젝트 인원 수 증가
            webSocketProjectUserCountMapper.increaseCurrentUsersWithProjectId(projectId);
        }

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            log.trace("웹소켓 [SUBSCRIBE] 요청");
            // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크
            WebSocketUser webSocketUser = validateAndRetrieveUser(headerAccessor);

            // Destination 주소 검증하기
            EqualsSubscribeDestinationProjectId(headerAccessor, webSocketUser);
            // 구독하기
            subscribeChannel(headerAccessor, webSocketUser);
        }

        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            log.trace("웹소켓 [SEND] 요청");
            // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크
            WebSocketUser webSocketUser = validateAndRetrieveUser(headerAccessor);

            EqualsSubscribeDestinationProjectId(headerAccessor, webSocketUser);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * Destination 주소와 구독한 ProjectId가 동일한 지 확인하는 함수
     */
    private void EqualsSubscribeDestinationProjectId(StompHeaderAccessor headerAccessor, WebSocketUser webSocketUser) {
        log.trace("웹 소켓 Destination 주소가 같은지 판별");
        // 1. 사용자가 구독신청한 subscribeProjectId 반환
        String[] split = headerAccessor.getDestination().toString().split("/");
        String subscribeProjectId = split[1].equals("user") ? split[4] : split[3];

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
     * 만약 구독 되어 있다면 예외던짐.
     * 클라이언트가 채팅/터미널/커서 채널에 구독하기
     */
    private void subscribeChannel(StompHeaderAccessor headerAccessor, WebSocketUser webSocketUser) {
        String[] split = headerAccessor.getDestination().toString().split("/");
        // /user로 시작하는 경우 chatuser
        String subscribeType = split[1].equals("user") ? split[1] + split[5] : split[4];
        log.trace("SubscribeType = {}", subscribeType);

        // 이미 구독한 상태라면
        if (webSocketUser.isSubscribe(subscribeType)) {
            log.warn("웹소켓 구독 중복 에러 입니다. subscribeType = {}", subscribeType);
            throw new BaseException("이미 구독한 채널 입니다.");
        }

        // 구독안했으면 구독해주기
        webSocketUser.startSubscribe(subscribeType);
    }


    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     */
    private WebSocketUser validateAndRetrieveUser(StompHeaderAccessor headerAccessor) {
        String sessionId = getSessionId(headerAccessor);
        log.trace("웹소켓 검증 로직 실행, sessionId = {}", sessionId);

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        if (webSocketUser == null) {
            log.error("웹소켓에 없는 세션아이디를 가진 잘못된 사용자 접근입니다.");
            throw new BaseException("잘못된 사용자 접근");
        }

        return webSocketUser;
    }

    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     */
    private String getSessionId(StompHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");
        if (simpSessionAttributes == null) {
            log.trace("웹소켓 세션 아이디를 찾을 수 없습니다!");
            throw new BaseException("웹소켓 세션 아이디를 찾을 수 없습니다.");
        }

        return simpSessionAttributes.get("WebSocketUserSessionId");
    }
}
