package goorm.dbjj.ide.websocket.containerloading;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ContainerLoadingController {

    private final SimpMessagingTemplate template;

    /**
     * 컨테이너 로딩정보를 해당 프로젝트의 모든 유저에게 전송.
     * @param projectId, object
     * */
    public void broadcastContainerLoading(
            String projectId,
            Object object
    ){
        log.trace("웹소켓 [컨테이너] [로딩 상태] 전송, projectId = {}, object ={}", projectId, object);
        template.convertAndSend("/topic/project/"+projectId+"/container-loading",object);
    }
}
