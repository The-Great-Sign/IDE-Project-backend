package goorm.dbjj.ide.lambdahandler.executionoutput;

import goorm.dbjj.ide.container.ExecutionIdMapper;
import goorm.dbjj.ide.websocket.terminal.TerminalController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutputSendingServiceImpl implements OutputSendingService {

    private final ExecutionIdMapper executionIdMapper;
    private final TerminalController terminalController;

    @Override
    public void sendTo(LogEntry logEntry) {
        /**
         * 로그로부터 executionId를 획득합니다.
         */
        String executionId = logEntry.getLogStream();
        String message = logEntry.getLogEvents().get(0).getMessage();
        ExecutionOutputDto executionOutputDto = parseMessage(message);
        log.debug("output : {}", executionOutputDto);

        /**
         * executionId를 누가 어떤 프로젝트에서 수행했는지 가져옵니다.
         */
        ExecutionIdMapper.MappedInfo mappedInfo = executionIdMapper.get(executionId);

        if (mappedInfo == null) {
            log.error("실행 정보와 매칭되는 유저 정보가 없습니다. executionId : {}", executionId);
            return;
        }

        executionIdMapper.remove(executionId);

        /**
         * todo: 웹소켓으로 전송하는 로직을 구현합니다.
         * ws.send(projectId, userId, executionOutputDto);
         */
        terminalController.terminalExecutionResult(
                mappedInfo.getProjectId(),
                mappedInfo.getUserId(),
                executionOutputDto
        );
    }

    private ExecutionOutputDto parseMessage(String message) {

        String[] splitedMessage = message.split("\n");

        if(splitedMessage.length == 4) {
            return new ExecutionOutputDto(true, "", splitedMessage[1]);
        } else if (splitedMessage.length == 5) {
            return new ExecutionOutputDto(true, splitedMessage[1], splitedMessage[2]);
        } else {
            log.error("응답 메시지의 형식이 맞지 않습니다. message : {}", message);
            return new ExecutionOutputDto(false,"알 수 없는 오류가 발생했습니다.", "");
        }
    }
}
