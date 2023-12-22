package goorm.dbjj.ide.websocket.chatting;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.websocket.WebSocketUser;
import goorm.dbjj.ide.websocket.WebSocketUserSessionMapper;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingContentRequestDto;
import goorm.dbjj.ide.websocket.chatting.dto.ChattingResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;
    private final SimpMessagingTemplate template;
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;

    /**
     * 채팅방 subscribe 시 입장 알림 및 웹 소켓 세션 등록하기
     * */
    @SubscribeMapping("/project/{projectId}/chat")
    public void enter(
            SimpMessageHeaderAccessor headerAccessor,
            @DestinationVariable("projectId") String projectId
    ){
        // 세션 방식을 이용해서 유저 아이디 가져오기
        String userNickname = getUserNickname(headerAccessor);
        log.trace("웹소켓 [채팅] [입장] 메세지 출력 projectId = {}, nickName = {}", projectId, userNickname);

        ChattingResponseDto enterMessage = chattingService.enter(projectId, userNickname);
        template.convertAndSend("/topic/project/"+projectId+"/chat",enterMessage);
    }

    /**
     * 채팅방 subscribe 시 입장 알림
     * */
    @MessageMapping("/project/{projectId}/chat-create")
    @SendTo("/topic/project/{projectId}/chat")
    public ChattingResponseDto talk(
            SimpMessageHeaderAccessor headerAccessor,
            @Payload ChattingContentRequestDto chatsDtoChattingContentRequestDto
    ) {
        // 세션 방식을 이용해서 유저 아이디 가져오기
        String userNickname = getUserNickname(headerAccessor);
        String projectId = getProjectId(headerAccessor);

        log.trace("웹소켓 [채팅] [메세지] 출력 chatsDtoChattingContentRequestDto = {}, projectId = {}, nickName = {}", chatsDtoChattingContentRequestDto, projectId, userNickname);

        return chattingService.talk(chatsDtoChattingContentRequestDto, userNickname, projectId);
    }

    /**
     * 퇴장 메세지 컨트롤러
     * 인터셉터에서 WebSocketProjectUserCountMapper의 인원이 1명 이상일 경우 호출됨.
     * */
    public void exit(
            String projectId,
            String nickname
    ){
        log.trace("웹소켓 [채팅] [퇴장] 메세지 출력 projectId = {}, nickName = {}", projectId, nickname);
        ChattingResponseDto exitMessage = chattingService.exit(nickname, projectId);
        template.convertAndSend("/topic/project/" + projectId + "/chat", exitMessage);
    }

    /**
     * headerAccessor에서 세션아이디 추출 및
     * */
    private String getSessionId(SimpMessageHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");
        if(simpSessionAttributes == null){
            log.trace("웹소켓 세션 아이디를 찾을 수 없습니다!");
            throw new BaseException("웹소켓 세션 아이디를 찾을 수 없습니다.");
        }

        return simpSessionAttributes.get("WebSocketUserSessionId");
    }

    /**
     * 세션 아이디를 이용해서 유저 아이디 반환해주는 메서드
     * */
    private String getUserNickname(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = getSessionId(headerAccessor);

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        return webSocketUser.getUserInfoDto().getNickname();
    }

    /**
     * 세션 아이디를 이용해서 프로젝트 아이디 반환해주는 메서드
     * */
    private String getProjectId(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = getSessionId(headerAccessor);

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        return webSocketUser.getProjectId();
    }
}
