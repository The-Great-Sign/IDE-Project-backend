package goorm.dbjj.ide.lambdahandler.containerstatus;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerInfo;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerStatusChangeRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerStatusService {

    private final MemoryContainerRepository memoryContainerRepository;

    /**
     * 전달받은 로그로부터 MemoryContainerRepository의 정보를 변경합니다.
     * @param requestDto
     */
    public void changeContainerStatus(ContainerStatusChangeRequestDto requestDto) {
        String changeStatus = requestDto.getContainerStatus();

        // 컨테이너 ID를 통해 프로젝트 ID를 획득합니다.
        String projectId = memoryContainerRepository.findProjectId(requestDto.getTaskArn());

        // projectId가 없다면, 실행된 적이 없는 프로젝트로부터 변경사항이 전달된 것으로 에러를 발생시켜야합니다.
        if(projectId == null) {
            log.error("실행중이지 않은 프로젝트로부터 변경사항이 전달되었습니다. 빠른 확인 부탁드립니다.");
            throw new BaseException("실행중이지 않은 프로젝트로부터 변경사항이 전달되었습니다. 빠른 확인 부탁드립니다.");
        }

        // 전달된 Status에 대해서 각기 다른 처리를 해줍니다.
        if(changeStatus.equals("RUNNING")) {
            /**
             * TODO: 해당 컨테이너 ID를 통해 Project를 식별한 뒤, 해당 프로젝트의 로딩 상태를 구독한 사용자들에게 전달한다.
             */
            log.debug("projectId : {}", projectId);
            ContainerInfo containerInfo = memoryContainerRepository.find(projectId);
            containerInfo.setRunning();
            log.debug("containerInfo : {}", containerInfo);

            // 이후 projectId를 통해 해당 프로젝트의 로딩 상태를 구독한 사용자들에게 전달한다.

        } else if (changeStatus.equals("PENDING")) {
            memoryContainerRepository.find(projectId).setPending();
        }
    }
}
