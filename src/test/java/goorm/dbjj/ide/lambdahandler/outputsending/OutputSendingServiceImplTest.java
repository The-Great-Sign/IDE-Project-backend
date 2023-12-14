package goorm.dbjj.ide.lambdahandler.outputsending;

import goorm.dbjj.ide.container.ExecutionIdMapper;
import goorm.dbjj.ide.lambdahandler.executionoutput.OutputSendingService;
import goorm.dbjj.ide.lambdahandler.executionoutput.OutputSendingServiceImpl;
import goorm.dbjj.ide.lambdahandler.executionoutput.LogEntry;
import goorm.dbjj.ide.lambdahandler.executionoutput.LogEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class OutputSendingServiceImplTest {
    ExecutionIdMapper executionSessionIdMapper = new ExecutionIdMapper();
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