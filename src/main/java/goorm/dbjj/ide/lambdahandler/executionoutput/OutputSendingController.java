package goorm.dbjj.ide.lambdahandler.executionoutput;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OutputSendingController {

    @Value("${aws.lambda.secretKey}")
    private String secretKey;

    private final OutputSendingService outputSendingService;

    /**
     * Lambda로부터 로그를 전송받습니다.
     * 이후 로그를 사용자에게 전달합니다.
     * @param logEntry 로그 정보가 담겨있습니다.
     * @param requestSecretKey lambda에서 보내는 secretKey로, 이 값이 일치해야만 로그를 전송합니다.
     *                        외부에서 API를 악성 호출하는 것을 방지합니다.
     */
    @PostMapping("/api/execution/output")
    public ResponseEntity<Void> sendOutput(
            @RequestBody LogEntry logEntry,
            @RequestParam("secretKey") String requestSecretKey
    ) {
//        log.debug("logEntry : {}", logEntry);
        log.debug("logEntry: {}", logEntry.getLogStream());

        if(requestSecretKey == null || !requestSecretKey.equals(secretKey)) {
            log.warn("secretKey가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        outputSendingService.sendTo(logEntry);

        return ResponseEntity.ok().build();
    }
}
