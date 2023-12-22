package goorm.dbjj.ide.websocket.filedirectory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketFileDirectoryController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 파일 디렉터리 관련 모든 이벤트를 클라이언트에 전송하는 로직
     * @param projectId, object
    * */
    public void broadcastFileAndDirectoryDetails(
        String projectId,
        Object object
    ){
        log.trace("웹소켓 [디렉터리] 변경 상태 전송, projectId = {}, object ={}", projectId, object);
        simpMessagingTemplate.convertAndSend("/topic/project/" + projectId + "/file", object);
    }
}
