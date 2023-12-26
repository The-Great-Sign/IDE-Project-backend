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
    private final LogicalDirectoryExtractor extractor;

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
     * 메시지를 파싱합니다.
     * @param message
     * @return
     */
    private ExecutionOutputDto parseMessage(String message) {

        String[] splitedMessage = message.split(separator);

        for(int i = 0; i < splitedMessage.length; i++) {
            splitedMessage[i] = splitedMessage[i].trim();
        }

        try {
            return switch (splitedMessage.length) {
                case 1 -> new ExecutionOutputDto(true, "", extractor.extract(splitedMessage[0]));
                case 2 -> new ExecutionOutputDto(true, contentWrapper(splitedMessage[0]), extractor.extract(splitedMessage[1]));
                default -> new ExecutionOutputDto(false, "알 수 없는 오류가 발생했습니다.", "/");
            };
        } catch (Exception e) {
            log.debug("메시지 파싱 중 오류가 발생했습니다. : {}", e.getMessage());
            return new ExecutionOutputDto(false, "알 수 없는 오류가 발생했습니다.", "/");
        }
    }

    private String contentWrapper(String content) {
        if(content.startsWith("/app")) {
            String newContent = content.substring(4);
            if(newContent.startsWith("/")) {
                return newContent;
            } else {
                return "/" + newContent;
            }
        } else {
            return content;
        }
    }
}
