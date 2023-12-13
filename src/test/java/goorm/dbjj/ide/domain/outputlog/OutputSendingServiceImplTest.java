package goorm.dbjj.ide.domain.outputlog;

import goorm.dbjj.ide.container.ExecutionSessionIdMapper;
import goorm.dbjj.ide.domain.outputlog.dto.request.LogEntry;
import goorm.dbjj.ide.domain.outputlog.dto.request.LogEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class OutputSendingServiceImplTest {
    ExecutionSessionIdMapper executionSessionIdMapper = new ExecutionSessionIdMapper();
    OutputSendingService outputSendingService = new OutputSendingServiceImpl(executionSessionIdMapper);
    @Test
    void outputSendingServiceTest() {

        LogEntry logEntry = new LogEntry(
                "messageType",
                "owner",
                "logGroupName",
                "logStreamName",
                List.of("filter"),
                List.of(
                        new LogEvent(
                                "id",
                                1234,
                                "FirstLog\ncontent\n/path\n\nhello"
                        )
                )
        );

        executionSessionIdMapper.put(
                "logStreamName",
                "projectId",
                "userId"
        );

        assertThat(executionSessionIdMapper.get("logStreamName")).isNotNull();

        outputSendingService.sendTo(logEntry);

        assertThat(executionSessionIdMapper.get("logStreamName")).isNull();
    }
}