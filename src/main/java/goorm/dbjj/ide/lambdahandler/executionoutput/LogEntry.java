package goorm.dbjj.ide.lambdahandler.executionoutput;

import lombok.*;

import java.util.List;

/**
 * Lambda로부터 가져온 메시지를 바인딩하는 클래스입니다
 * 실질적은 로그의 위치는 LogMessage의 logEvents에 담겨있습니다.
 * Execution의 ID는 logStream입니다.
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LogEntry {
    private String messageType;
    private String owner;
    private String logGroup;
    private String logStream;
    private List<String> subscriptionFilters;
    private List<LogEvent> logEvents;
}

