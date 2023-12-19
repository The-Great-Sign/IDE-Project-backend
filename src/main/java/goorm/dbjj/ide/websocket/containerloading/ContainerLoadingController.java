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
     * 파일 디렉터리 관련 모든 이벤트를 클라이언트에 전송하는 로직
     * @param projectId, object
     * */
    public void broadcastContainerLoading(
            String projectId,
            Object object
    ){
        template.convertAndSend("/topic/project/"+projectId+"/container-loading",object);
    }
}
