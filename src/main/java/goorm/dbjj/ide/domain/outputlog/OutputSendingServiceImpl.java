package goorm.dbjj.ide.domain.outputlog;

import goorm.dbjj.ide.container.ExecutionSessionIdMapper;
import goorm.dbjj.ide.domain.outputlog.dto.request.LogEntry;
import goorm.dbjj.ide.domain.outputlog.dto.response.ExecutionOutputDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class OutputSendingServiceImpl implements OutputSendingService {

    private final ExecutionSessionIdMapper executionSessionIdMapper;

    @Override
    public void sendTo(LogEntry logEntry) {
        String sessionId = logEntry.getLogStream();
        String message = logEntry.getLogEvents().get(0).getMessage();
        ExecutionOutputDto executionOutputDto = parseMessage(message);
        log.debug("output : {}", executionOutputDto);

        ExecutionSessionIdMapper.MappedInfo mappedInfo = executionSessionIdMapper.get(sessionId);

        if (mappedInfo == null) {
            log.error("해당 세션에 매핑된 정보가 없습니다. sessionId : {}", sessionId);
            return;
        }

        executionSessionIdMapper.remove(sessionId);

        /**
         * todo: 웹소켓으로 전송하는 로직을 구현합니다.
         * ws.send(projectId, userId, executionOutputDto);
         */
    }

    private ExecutionOutputDto parseMessage(String message) {

        String[] splitedMessage = message.split("\n");

        if (splitedMessage.length != 5) {
            log.error("응답 메시지의 형식이 맞지 않습니다. Message : {}", message);
            return new ExecutionOutputDto(false,"", "알 수 없는 오류가 발생했습니다.");
        }

        String path = splitedMessage[1];
        String content = splitedMessage[2];

        return new ExecutionOutputDto(true, path, content);
    }
}
