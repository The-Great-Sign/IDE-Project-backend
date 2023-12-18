package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.websocket.dto.UserInfoDto;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;

    /**
     * 클라이언트가 보낸 STOMP 메세지 처리하기전 인터셉터
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.trace("WebSocketChannelInterceptor.preSend execute");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            List<String> authorization = headerAccessor.getNativeHeader("Authorization");
            String authorizationValue = authorization.get(0);

            // todo : 현정님 인증 하고 유저 정보 얻어주세요!
//        SecuritycontextHolder을 사용해서 인증된 사용자의 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        requset => 헤더에 토큰을 받아서 인증된 사용자면 인증된 사용자의 정보를 담아오는 것.
//        // 인증된 사용자인지 확인, 아니면 exception.
//        if (authentication == null && !authentication.isAuthenticated()) {
//            throw new BaseException("인증된 사용자가 아닙니다.");
//        }

            // todo : 현정님 여기에 유저정보 담아야해요.
            // 인증된 사용자의 세부 정보 불러오기.
            //User userDetails = (User) authentication.getPrincipal();
//            UserInfoDto userInfoDto = new UserInfoDto(userDetails);
            UserInfoDto userInfoDto = new UserInfoDto(1L, "이메일", "닉네임");
            log.trace("웹소켓 사용자 ID, 이름 가져오기 : {} {}", userInfoDto.getId(), userInfoDto.getNickname());

            // URL 마지막 부분에 존재하는 프로젝트ID 가져오기
            String[] split = getUri(headerAccessor).split("/");
            String projectId = split[split.length - 1];
            log.trace("{}", projectId);

    /*        //// db 없이 테스트할때 주석 처리 해야함
            Optional<Project> projectOptional = projectRepository.findById(projectId);
            // 프로젝트가 없을 경우
            if (projectOptional.isEmpty()) {
                log.error("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
                throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
            }

            // 프로젝트에 참여한 유저가 아닐 경우
            if (!projectUserRepository.existsByProjectAndUser(projectOptional.get(), userDetails)) {
                log.error("해당 프로젝트의 소유권 없는자가 접근했습니다.");
                throw new BaseException("웹소켓에 연결될 프로젝트가 존재하지 않습니다.");
            }

            // 이미 실행중인 프로젝트인지 검증
            if (webSocketUserSessionMapper.existsByProjectAndUser(userInfoDto, projectId)) {
                log.warn("이미 실행중인 프로젝트 입니다.");
                throw new BaseException("이미 실행중인 프로젝트 입니다!");
            }
*/
            // 세션등록
            String sessionId = getSessionId(headerAccessor);
            webSocketUserSessionMapper.put(sessionId, new WebSocketUser(userInfoDto, projectId));
        }


        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크
            WebSocketUser webSocketUser = validateAndRetrieveUser(headerAccessor);

            // Destination 주소 검증하기
            EqualsSubscribeDestinationProjectId(headerAccessor, webSocketUser);
            // 구독하기
            subscribeChannel(headerAccessor, webSocketUser);
        }

        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
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
        // 1. 사용자가 구독신청한 subscribeProjectId 반환
        String[] split = headerAccessor.getDestination().toString().split("/");
        String subscribeProjectId = split[1].equals("user") ? split[4] : split[3];
        log.trace("subscribe projectId = {}", subscribeProjectId);

        // 2. 사용자가 구독한 projectId 반환
        String projectId = webSocketUser.getProjectId();
        log.trace("subscribe projectId = {}", projectId);

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if (!subscribeProjectId.equals(projectId)) {
            log.warn("WebSocketChannelInterceptor.preSend 접속한 프로젝트와 구독하고자하는 프로젝트가 다릅니다");
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
    }

    /**
     * 클라이언트가 채팅/터미널/커서 채널에 구독하기
     * * @return  true : 구독한 상태, false : 구독안한 상태
     */
    private void subscribeChannel(StompHeaderAccessor headerAccessor, WebSocketUser webSocketUser) {
        String[] split = headerAccessor.getDestination().toString().split("/");
        // /user로 시작하는 경우 chatuser
        String subscribeType = split[1].equals("user") ? split[1] + split[5] : split[4];
        log.trace("SubscribeType = {}", subscribeType);

        // 이미 구독한 상태라면
        if (webSocketUser.isSubscribe(subscribeType)) {
            log.warn("이미 구독한 채널 입니다.");
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
        log.trace("sessionId = {}", sessionId);

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        if (webSocketUser == null) {
            log.error("WebSocketChannelInterceptor.preSend 잘못된 사용자 접근입니다.");
            throw new BaseException("잘못된 사용자 접근");
        }

        return webSocketUser;
    }

    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     */
    private String getSessionId(StompHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");

        return simpSessionAttributes.get("WebSocketUserSessionId");
    }

    /**
     * 클라이언트가 보낸 URI 를 가져오는 명령어
     */
    private String getUri(StompHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");

        return simpSessionAttributes.get("uri");
    }
}
