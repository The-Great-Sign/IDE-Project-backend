package goorm.dbjj.ide.websocket.terminal;

import goorm.dbjj.ide.lambdahandler.executionoutput.ExecutionOutputDto;
import goorm.dbjj.ide.websocket.WebSocketUser;
import goorm.dbjj.ide.websocket.WebSocketUserSessionMapper;
import goorm.dbjj.ide.websocket.terminal.dto.TerminalExecuteRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TerminalController {
    private final TerminalService terminalService;
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 터미널 실행하기!
     * */
    @MessageMapping("/project/{projectId}/terminal")
    public void executeTerminal(
            SimpMessageHeaderAccessor headerAccessor,
            @DestinationVariable("projectId") String projectId,
            @Payload TerminalExecuteRequestDto terminalExecuteRequestDto
    ){
        log.trace("executeTerminal execute");
        Long userId = getUserId(headerAccessor);
        terminalService.executeTerminal(terminalExecuteRequestDto,projectId,userId);
    }


    /**
     * 터미널 결과 연결하는 부분 호출하기
     * */
    public void terminalExecutionResult(String projectId, Long userId, ExecutionOutputDto executionOutputDto){
        log.trace("terminalExecutionResult execute");
        String sessionId = webSocketUserSessionMapper.findSessionIdByProjectAndUserInfoDto(userId, projectId);

        simpMessagingTemplate.convertAndSendToUser(sessionId,"/queue/project/"+projectId +"/terminal", executionOutputDto);
    }

    /**
     * headerAccessor에서 세션아이디 추출 및
     * 세션 아이디를 이용해서 유저 아이디 반환해주는 메서드
     * */
    private Long getUserId(SimpMessageHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");
        String uuid = simpSessionAttributes.get("WebSocketUserSessionId");

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(uuid);
        return webSocketUser.getUserInfoDto().getId();
    }
}
