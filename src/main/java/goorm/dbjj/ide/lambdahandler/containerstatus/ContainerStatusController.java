package goorm.dbjj.ide.lambdahandler.containerstatus;

import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerStatusChangeRequestDto;
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
public class ContainerStatusController {

    @Value("${aws.lambda.secretKey}")
    private String secretKey;

    private final ContainerStatusService containerStatusService;

    /**
     * 컨테이너 로딩이 완료되었음을 알리는 요청을 받는 API입니다.
     * @param containerStatusChangeRequestDto 컨테이너 상태 변경 요청 정보가 담겨있습니다.
     * @param requestSecretKey lambda에서 보내는 secretKey로, 이 값이 일치해야만 로그를 전송합니다.
     * @return
     */
    @PostMapping("/api/container/load")
    public ResponseEntity<Void> getContainerStatus(
            @RequestBody ContainerStatusChangeRequestDto containerStatusChangeRequestDto,
            @RequestParam("secretKey") String requestSecretKey
    ) {
        log.trace("getContainerStatus called");
        log.debug("containerStatusChangeRequestDto : {}", containerStatusChangeRequestDto);

        if(requestSecretKey == null || !requestSecretKey.equals(secretKey)) {
            log.warn("secretKey가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        containerStatusService.changeContainerStatus(containerStatusChangeRequestDto);

        return ResponseEntity.ok().build();
    }
}
