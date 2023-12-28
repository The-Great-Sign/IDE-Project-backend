package goorm.dbjj.ide.websocket;

import goorm.dbjj.ide.api.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final WebSocketChannelInterceptorHandler webSocketChannelInterceptorHandler;


    /**
     * 클라이언트가 보낸 STOMP 메세지 처리하기전 인터셉터
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            webSocketChannelInterceptorHandler.stompConnect(
                    getStompHeaderValue(headerAccessor,"Authorization"),
                    getStompHeaderValue(headerAccessor,"ProjectId"),
                    getSessionId(headerAccessor)
            );
        } else if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            webSocketChannelInterceptorHandler.stompSubscribe(
                    validateAndRetrieveUser(headerAccessor),
                    getSubscribeProjectId(headerAccessor),
                    getSubscribeType(headerAccessor)
            );
        } else if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            webSocketChannelInterceptorHandler.stompSend(
                    validateAndRetrieveUser(headerAccessor),
                    getSubscribeProjectId(headerAccessor),
                    getSubscribeType(headerAccessor)
            );
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * 클라이언트가 보낸 STOMP 헤더를 추출하는 메서드
     * */
    private String getStompHeaderValue(
            StompHeaderAccessor headerAccessor,
            String HeaderType
    ) {
        List<String> authorization = headerAccessor.getNativeHeader(HeaderType);
        return authorization.get(0);
    }

    /**
     * StompHeader에서 Destination의  projectId 추출하는 로직.
     * */
    private String getSubscribeProjectId(StompHeaderAccessor headerAccessor) {
        String[] split = headerAccessor.getDestination().toString().split("/");
        return split[1].equals("user") ? split[4] : split[3];
    }

    /**
     * StompHeader에서 Destination의  subscribeType 추출하는 로직.
     * */
    private String getSubscribeType(StompHeaderAccessor headerAccessor) {
        String[] split = headerAccessor.getDestination().toString().split("/");
        // /user로 시작하는 경우 chatuser
        return split[1].equals("user") ? split[1] + split[5] : split[4];
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