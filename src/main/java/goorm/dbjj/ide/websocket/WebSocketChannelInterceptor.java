package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    private final WebSocketUserSessionMapper webSocketUserSessionMapper;

    /**
     * 클라이언트가 보낸 STOMP 메세지 처리하기전 인터셉터
     * */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            validateAndRetrieveUser(headerAccessor);
        }

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand()) || StompCommand.SEND.equals(headerAccessor.getCommand())) {
            // 1. 사용자가 구독신청한 subscribeProjectId 반환
            String[] split = headerAccessor.getDestination().toString().split("/");
            Long subscribeProjectId = Long.valueOf(split[3]);
            log.trace("subscribe projectId = {}", subscribeProjectId);

            // 2. 사용자가 구독한 projectId 반환
            Long projectId = validateAndRetrieveUser(headerAccessor).getProjectId();
            log.trace("subscribe projectId = {}", projectId);

            // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
            if(!subscribeProjectId.equals(projectId)){
                log.warn("WebSocketChannelInterceptor.preSend 접속한 프로젝트와 구독하고자하는 프로젝트가 다릅니다");
                throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
            }
        }


        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     * */
    private WebSocketUser validateAndRetrieveUser(StompHeaderAccessor headerAccessor) {
        String uuid = getUUID(headerAccessor);

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(uuid);
        if(webSocketUser == null){
            log.warn("WebSocketChannelInterceptor.preSend 잘못된 사용자 접근입니다.");
            throw new BaseException("잘못된 사용자 접근");
        }

        return webSocketUser;
    }

    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     * */
    private String getUUID(StompHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");

        return simpSessionAttributes.get("WebSocketUserSessionId");
    }
}
