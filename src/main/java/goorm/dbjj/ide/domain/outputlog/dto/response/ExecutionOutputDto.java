package goorm.dbjj.ide.domain.outputlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 사용자가 수행한 결과를 제공하는 클래스입니다.
 */
@ToString
@Getter
public class ExecutionOutputDto {
    private boolean success;
    private String path;
    private String content;

    public ExecutionOutputDto(boolean success, String content, String path) {
        this.success = success;
        this.content = content;
        this.path = path;
    }
}
