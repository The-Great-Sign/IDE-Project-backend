package goorm.dbjj.ide.lambdahandler.executionoutput;

import goorm.dbjj.ide.websocket.terminal.TerminalController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutputSendingServiceImpl implements OutputSendingService {

    @Value("${app.outputSeparator}")
    private String separator;

    private final TerminalController terminalController;

    @Override
    public void sendTo(String executionOutput, String projectId, Long userId) {

        ExecutionOutputDto executionOutputDto = parseMessage(executionOutput);
        log.debug("output : {}", executionOutputDto);

        //웹소켓으로 전송합니다.
        terminalController.terminalExecutionResult(
                projectId,
                userId,
                executionOutputDto
        );
    }

    /**
     * 이 private 메서드로 인해 테스트가 어려워지는 문제 발생
     * 추후 분리 예정
     * @param message
     * @return
     */
    private ExecutionOutputDto parseMessage(String message) {

        String[] splitedMessage = message.split(separator);

        for(int i = 0; i < splitedMessage.length; i++) {
            splitedMessage[i] = splitedMessage[i].trim();
        }

        return switch (splitedMessage.length) {
            case 1 -> new ExecutionOutputDto(true, "", extractLogicalAddress(splitedMessage[0]));
            case 2 -> new ExecutionOutputDto(true, splitedMessage[0], extractLogicalAddress(splitedMessage[1]));
            default -> new ExecutionOutputDto(false, "알 수 없는 오류가 발생했습니다.", "");
        };
    }

    /**
     * 도커 path에서 사용자의 상대 주소를 추출합니다.
     * @param path
     * @return
     */
    private String extractLogicalAddress(String path) {
        String logicalDirectory = path.substring(path.indexOf("/app")+ 4);
        return logicalDirectory.isEmpty() ? "/" : logicalDirectory;
    }
}
