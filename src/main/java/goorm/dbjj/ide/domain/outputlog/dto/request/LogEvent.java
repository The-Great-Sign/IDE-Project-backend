package goorm.dbjj.ide.domain.outputlog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LogEvent {
    private String id;
    private long timestamp;
    private String message;
}