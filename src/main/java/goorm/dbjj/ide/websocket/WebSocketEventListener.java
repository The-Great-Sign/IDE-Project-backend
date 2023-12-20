package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.websocket.chatting.ChattingController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * STOMP 이벤트 리스너
 * disconnect 시에 채팅방에 퇴장알림을 위해 추가한 이벤트 리스너입니다.
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChattingController chattingController;
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final WebSocketProjectUserCountMapper webSocketProjectUserCountMapper;
    private final ContainerService containerService;
    private final ProjectRepository projectRepository;

    /**
     * DisConnect 시 채팅방 퇴장 알림 기능 구현 및 WebSocketUserSessionMapper에 존재하는 유저 정보 없애기!
     * */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.trace("WebSocketEventListener.handleWebSocketDisconnectListener execute");

        // simpSessionAttributes에 존재하는 uuid 가져오기
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");
        String sessionId = simpSessionAttributes.get("WebSocketUserSessionId");

        // WebSocketUserSessionMapper 에 해당하는 유저 sessionId 없애기
        WebSocketUser removeWebSocketUser = webSocketUserSessionMapper.remove(sessionId);

        // webSocketProjectUserCountMapper 해당 projectId의 인원 감소 로직
        webSocketProjectUserCountMapper.decreaseCurrentUsersWithProjectId(removeWebSocketUser.getProjectId());

        //인원이 0명인 경우 에러 처리
        if (webSocketProjectUserCountMapper.getCurrentUsersByProjectId(removeWebSocketUser.getProjectId()) == null || webSocketProjectUserCountMapper.getCurrentUsersByProjectId(removeWebSocketUser.getProjectId()) == 0L) {
            log.trace("프로젝트ID {{}} 현재인원 0명으로 프로젝트를 종료합니다.", removeWebSocketUser.getProjectId());
            // webSocketProjectUserCountMapper의 유저
            webSocketProjectUserCountMapper.removeCurrentUsersByProjectId(removeWebSocketUser.getProjectId());

            // 특정 프로젝트에 현재 인원 0 명일 경우 프로젝트 종료 로직
            Project project = projectRepository.findById(removeWebSocketUser.getProjectId()).orElseThrow(() -> new BaseException("프로젝트가 존재하지 않습니다"));
            containerService.stopContainer(project);
        } else {
            log.trace("채팅 퇴장 알림 실행");
            chattingController.exit(removeWebSocketUser.getProjectId(), removeWebSocketUser.getUserInfoDto().getNickname());
        }
    }
}