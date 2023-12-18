package goorm.dbjj.ide.lambdahandler.outputsending;

import goorm.dbjj.ide.container.ExecutionIdMapper;
import goorm.dbjj.ide.lambdahandler.executionoutput.*;
import goorm.dbjj.ide.websocket.terminal.TerminalController;
import goorm.dbjj.ide.websocket.terminal.dto.TerminalExecuteRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class OutputSendingServiceImplTest {
    ExecutionIdMapper executionSessionIdMapper = new ExecutionIdMapper();

    static class MockTerminalController extends TerminalController {
        public MockTerminalController() {
            super(null, null, null);
        }

        @Override
        public void executeTerminal(SimpMessageHeaderAccessor headerAccessor, String projectId, TerminalExecuteRequestDto terminalExecuteRequestDto) {

        }

        @Override
        public void terminalExecutionResult(String projectId, Long userId, ExecutionOutputDto executionOutputDto) {

        }
    }
    OutputSendingService outputSendingService = new OutputSendingServiceImpl(
            executionSessionIdMapper,
            new MockTerminalController()
    );
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
                                "FirstLog\ncontent\n/app/path\n\nhello"
                        )
                )
        );

        executionSessionIdMapper.put(
                "logStreamName",
                "projectId",
                1L
        );

        assertThat(executionSessionIdMapper.get("logStreamName")).isNotNull();

        outputSendingService.sendTo(logEntry);

        assertThat(executionSessionIdMapper.get("logStreamName")).isNull();
    }
}