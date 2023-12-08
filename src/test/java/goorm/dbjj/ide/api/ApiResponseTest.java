package goorm.dbjj.ide.api;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ApiResponseTest {

    @Test
    void ok() {
        ApiResponse<String> response = ApiResponse.ok("test");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("요청에 성공했습니다.");
        assertThat(response.getResults()).isEqualTo("test");
    }

    @Test
    void okWithMessage() {
        ApiResponse<String> response = ApiResponse.ok("test", "test message");
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("test message");
        assertThat(response.getResults()).isEqualTo("test");
    }

    @Test
    void fail() {
        ApiResponse<String> response = ApiResponse.fail("test message");
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("test message");
        assertThat(response.getResults()).isNull();
    }
}