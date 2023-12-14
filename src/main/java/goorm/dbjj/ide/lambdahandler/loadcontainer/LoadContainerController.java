package goorm.dbjj.ide.lambdahandler.loadcontainer;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.status.MemoryContainerRepository;
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
public class LoadContainerController {

    @Value("${aws.lambda.secretKey}")
    private String secretKey;

    private final MemoryContainerRepository memoryContainerRepository;

    /**
     * 컨테이너 로딩이 완료되었음을 알리는 요청을 받는 API입니다.
     * @param containerStatusChangeRequestDto 컨테이너 상태 변경 요청 정보가 담겨있습니다.
     * @param requestSecretKey lambda에서 보내는 secretKey로, 이 값이 일치해야만 로그를 전송합니다.
     * @return
     */
    @PostMapping("/api/container/load")
    public ResponseEntity<Void> loadContainerComplete(
            @RequestBody ContainerStatusChangeRequestDto containerStatusChangeRequestDto,
            @RequestParam("secretKey") String requestSecretKey
    ) {
        log.debug("containerStatusChangeRequestDto : {}", containerStatusChangeRequestDto);

        if(requestSecretKey == null || !requestSecretKey.equals(secretKey)) {
            log.warn("secretKey가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String changeStatus = containerStatusChangeRequestDto.getContainerStatus();

        // 컨테이너 ID를 통해 프로젝트 ID를 획득합니다.
        String projectId = memoryContainerRepository.findProjectId(containerStatusChangeRequestDto.getTaskArn());

        // projectId가 없다면, 실행된 적이 없는 프로젝트로부터 변경사항이 전달된 것으로 에러를 발생시켜야합니다.
        if(projectId == null) {
            log.error("존재하지 않는 프로젝트입니다.");
            throw new BaseException("실행중이지 않은 프로젝트로부터 변경사항이 전달되었습니다. 빠른 확인 부탁드립니다.");
        }

        // 전달된 Status에 대해서 각기 다른 처리를 해줍니다.
        if(changeStatus.equals("RUNNING")) {
            /**
             * TODO: 해당 컨테이너 ID를 통해 Project를 식별한 뒤, 해당 프로젝트의 로딩 상태를 구독한 사용자들에게 전달한다.
             */
            log.debug("projectId : {}", projectId);
            memoryContainerRepository.find(projectId).setRunning();

            // 이후 projectId를 통해 해당 프로젝트의 로딩 상태를 구독한 사용자들에게 전달한다.
        } else if (changeStatus.equals("PENDING")) {
            memoryContainerRepository.find(projectId).setPending();
        }

        return ResponseEntity.ok().build();
    }
}
