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
import org.springframework.util.FastByteArrayOutputStream;

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
        log.trace("WebSocketChannelInterceptor.preSend execute");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        // 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크
        WebSocketUser webSocketUser = validateAndRetrieveUser(headerAccessor);

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            // 사용자가 이미 구독한 방일 경우에 그냥 메세지 전달 취소
            if(isAlreadySubscribe(headerAccessor, webSocketUser)){
                log.warn("이미 구독한 채널 입니다.");
                throw new BaseException("이미 구독한 채널 입니다.");
            }
            EqualsSubscribeDestinationProjectId(headerAccessor, webSocketUser);
        }

        if(StompCommand.SEND.equals(headerAccessor.getCommand())){
            EqualsSubscribeDestinationProjectId(headerAccessor, webSocketUser);
        }
        
        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * Destination 주소와 구독한 ProjectId가 동일한 지 확인하는 함수
     * */
    private void EqualsSubscribeDestinationProjectId(StompHeaderAccessor headerAccessor, WebSocketUser webSocketUser) {
        // 1. 사용자가 구독신청한 subscribeProjectId 반환
        String[] split = headerAccessor.getDestination().toString().split("/");
        Long subscribeProjectId = Long.valueOf(split[3]);
        log.trace("subscribe projectId = {}", subscribeProjectId);

        // 2. 사용자가 구독한 projectId 반환
        Long projectId = webSocketUser.getProjectId();
        log.trace("subscribe projectId = {}", projectId);

        // 1번과 2번을 비교하여 같을 경우 구독번호를 반환한다.
        if(!subscribeProjectId.equals(projectId)){
            log.warn("WebSocketChannelInterceptor.preSend 접속한 프로젝트와 구독하고자하는 프로젝트가 다릅니다");
            throw new BaseException("해당 프로젝트로 접근할 수 없습니다.");
        }
    }

    /**
    * 클라이언트가 채팅/터미널/커서 채널에 구독했는지 확인하는 코드
    * */
    private boolean isAlreadySubscribe(StompHeaderAccessor headerAccessor, WebSocketUser webSocketUser) {
        String[] split = headerAccessor.getDestination().toString().split("/");
        String subscribeType = split[4];
        log.trace("SubscribeType = {}",subscribeType);
        if(subscribeType.equals("chat")){
            if(webSocketUser.getIsChattingSubscribe()) return true;
            webSocketUser.SubscribeChatting();
        } else if(subscribeType.equals("terminal")){
            if(webSocketUser.getIsTerminal()) return true;
            webSocketUser.SubscribeTerminal();
        } else if(subscribeType.equals("cursor")){
            if(webSocketUser.getIsCursor()) return true;
            webSocketUser.SubscribeCursor();
        }
        return false;
    }


    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     * */
    private WebSocketUser validateAndRetrieveUser(StompHeaderAccessor headerAccessor) {
        String sessionId = getSessionId(headerAccessor);
        log.trace("sessionId = {}", sessionId);

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(sessionId);
        if(webSocketUser == null){
            log.warn("WebSocketChannelInterceptor.preSend 잘못된 사용자 접근입니다.");
            throw new BaseException("잘못된 사용자 접근");
        }

        return webSocketUser;
    }

    /**
     * 클라이언트가 보낸 STOMP 메세지의 사용자가 유효한 사용자인지 체크하는 로직
     * */
    private String getSessionId(StompHeaderAccessor headerAccessor) {
        ConcurrentHashMap<String, String> simpSessionAttributes = (ConcurrentHashMap<String, String>) headerAccessor.getMessageHeaders().get("simpSessionAttributes");

        return simpSessionAttributes.get("WebSocketUserSessionId");
    }
}
