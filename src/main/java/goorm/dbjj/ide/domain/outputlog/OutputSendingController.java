package goorm.dbjj.ide.domain.outputlog;

import goorm.dbjj.ide.domain.outputlog.dto.request.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OutputSendingController {

    private final OutputSendingService outputSendingService;

    @PostMapping("/api/execution/output")
    public void sendOutput(@RequestBody LogEntry logEntry) {
        log.debug("logEntry : {}", logEntry);
        outputSendingService.sendTo(logEntry);
    }
}
