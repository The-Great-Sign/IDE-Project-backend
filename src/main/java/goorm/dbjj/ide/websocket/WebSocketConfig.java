package goorm.dbjj.ide.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 웹소캣 관련 기본 설정하기!
 * */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시지 브로커가 활성화됨, stomp 메시징 사용 가능.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // 소켓 연결을 구성

    /**
     * 메시지 브로커의 구성 정의
     * */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/project"); // 메시지 브로커를 설정
        config.setApplicationDestinationPrefixes("/app","/topic"); // 서버에 접속하는 접두사 설정
    }

    /**
     * STOMP endPoint 등록하기!
     * */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 테스트용
        registry.addEndpoint("/ws/ide")
                .setAllowedOriginPatterns("*");
//				.setHandshakeHandler(new WebSocketHandShackHandler());

        // withSockJS사용용
        registry.addEndpoint("/ws/ide")
                .setAllowedOriginPatterns("*")
                .withSockJS();
//				.setInterceptors((ChatStompInterceptor) this.chatInterceptor);

        /*todo : endpoint로 connect 연결 시 인터셉터 등록하기 */

    }
    // todo : 채널 구독 인터셉터 등록하기
}
