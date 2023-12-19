package goorm.dbjj.ide.lambdahandler.outputsending;

import goorm.dbjj.ide.container.ExecutionIdMapper;
import goorm.dbjj.ide.lambdahandler.executionoutput.*;
import goorm.dbjj.ide.websocket.terminal.TerminalController;
import goorm.dbjj.ide.websocket.terminal.dto.TerminalExecuteRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class OutputSendingServiceImplTest {

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
            new MockTerminalController()
    );

    @Value("${app.outputSeparator}")
    private String separator;

    @Test
    @Disabled
    void outputSendingServiceTest() {
        String executionOutput = "Hello, World!" + separator + "/app";
        String projectId = "projectId";
        Long userId = 1L;

        outputSendingService.sendTo(executionOutput, projectId, userId);
    }
}