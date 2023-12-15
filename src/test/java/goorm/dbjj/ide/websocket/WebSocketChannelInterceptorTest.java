package goorm.dbjj.ide.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WebSocketChannelInterceptorTest {

    @Autowired
    WebSocketUserSessionMapper webSocketUserSessionMapper;

    @BeforeEach
    void setUp(){
        // 테스트에 사용될 WebSocketUserSessionMapper 객체 생성하기
        webSocketUserSessionMapper = new WebSocketUserSessionMapper();

        String testUUID1 = "test-uuid1";
        webSocketUserSessionMapper.put(testUUID1, new WebSocketUser(1L,1L));
    }
    @Test
    @DisplayName("UUID 통과 테스트")
    void CollectValidateAndRetrieveUser() {
        String testUUID = "test-uuid1";

        WebSocketUser webSocketUser = webSocketUserSessionMapper.get(testUUID);

        assertThat(webSocketUser.getUserId()).isEqualTo(1L);
        assertThat(webSocketUser.getProjectId()).isEqualTo(1L);
    }
}