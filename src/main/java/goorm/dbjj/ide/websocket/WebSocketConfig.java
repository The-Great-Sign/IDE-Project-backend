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
        config.setApplicationDestinationPrefixes("/app"); // 서버에 접속하는 접두사 설정
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

        /*todo : 고민되는 부분 정리하기
         *	1. 핸드쉐이크 과정에서 필터링을 어떻게할건지 =>
         *	2. 인터셉터를 도입하거나 http통신이니까 해결이 되는지 => 범석님 자료 읽기
         * */

    }
}
