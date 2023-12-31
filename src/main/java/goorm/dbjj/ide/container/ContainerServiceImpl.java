package goorm.dbjj.ide.container;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.command.CommandStringBuilder;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.lambdahandler.containerstatus.ContainerStore;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * IDE 컨테이너를 관리하는 서비스입니다.
 * https://www.notion.so/the-great-sign/3f49066725244273bde46265099f1242?pvs=4#5cdce6b1dee841769cd0b6acb2ea2b3e
 * <p>
 * - ContainerService는 다음과 같이 사용된다.
 * - ProjectService.createProject → 프로젝트를 생성하는데, 이 과정에서 RDS의 프로젝트의 메타데이터를 저장함과 동시에 사용될 컨테이너 이미지를 만들어야 한다.
 * - IDE 저장소와 협업 → IDE 세션에 한 명이라도 접근하면 컨테이너는 실행되어야 한다. IDE 세션에 아무도 존재하지 않는다면 컨테이너는 종료되어야 한다.
 * - 컨테이너의 중복 실행 문제를 막는다.
 * - 현재 사용자가 Container를 종료하고 실행할 수 있는 권한이 있는지 확인한다.
 * - 이 과정에서 ContainerService의 사용자가 직접적인 컨테이너와 관련된 정보를 모르도록 모든 외부 파라미터 정보는 Project로 받는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContainerServiceImpl implements ContainerService {

    private final ContainerUtil containerUtil;
    private final ContainerStore containerStore;
    private final CommandStringBuilder commandStringBuilder;

    /**
     * 컨테이너에 명령을 실행시킵니다.
     * 명령어로 나온 결과를 읽어 사용자에게 전달합니다.
     * - 어떤 사용자에게 결과를 보내야하는지 알아야 하므로 유저 정보를 가져와야 합니다.
     */
    @Override
    public void executeCommand(Project project, String path, String command, Long userId) {
        log.trace("ContainerService.executeCommand called");

        ContainerInfo containerInfo = containerStore.find(project.getId());

        if (containerInfo == null || !containerInfo.isRunning()) {
            throw new BaseException("컨테이너가 실행중이지 않습니다.");
        }

        containerUtil.executeCommand(
                containerInfo.getContainerId(),
                commandStringBuilder.createCommand(path,command, project.getId(), userId)
        );
    }

    /**
     * 프로젝트의 컨테이너 이미지를 생성합니다.
     * 생성 권한 검증은 이 메서드를 사용하는 측에서 미리 검증함을 전제로 합니다.
     *
     * @param project
     * @return 생성된 컨테이너 이미지의 ID
     */
    @Override
    public String createProjectImage(Project project) {
        log.trace("ContainerService.createProjectImage called");

        if (project.getContainerImageId() != null) {
            throw new BaseException("컨테이너 이미지가 존재합니다.");
        }

        return containerUtil.createContainerImage(
                project.getProgrammingLanguage(),
                project.getAccessPointId()
        );
    }

    /**
     * 프로젝트 컨테이너 이미지를 삭제합니다.
     * 삭제 권한은 이 메서드를 사용하는 측에서 검증해야합니다.
     * @param project
     */
    @Override
    public void deleteProjectImage(Project project) {
        log.trace("ContainerService.deleteProjectImage called");

        if (project.getContainerImageId() == null) {
            throw new BaseException("컨테이너 이미지가 존재하지 않습니다.");
        }

        containerUtil.deleteContainerImage(project.getContainerImageId());
        project.setContainerImageId(null);
    }

    /**
     * 프로젝트의 컨테이너를 실행시킵니다.
     * 만약 이미 실행중인 컨테이너가 있다면 예외를 발생시킵니다.
     * 실행 권한은 메서드를 사용하는 측에서 미리 검증함을 전제로 합니다.
     *
     * @param project
     */
    @Override
    public void runContainer(Project project) {
        log.trace("ContainerService.runContainer called");

        if (project.getContainerImageId() == null) {
            throw new BaseException("컨테이너 이미지가 존재하지 않습니다.");
        }

        if (containerStore.find(project.getId()).getContainerId() == null) {
            String containerId = containerUtil.runContainer(project.getContainerImageId());
            containerStore.save(project.getId(), containerId);
        } else {
            throw new BaseException("이미 실행중인 컨테이너가 있습니다.");
        }
    }

    /**
     * 컨테이너를 종료합니다.
     * 만약 컨테이너가 실행중이지 않다면 예외를 발생시킵니다.
     * 종료 권한은 메서드를 사용하는 측에서 미리 검증함을 전제로 합니다.
     *
     * @param project
     */
    @Override
    public void stopContainer(Project project) {
        log.trace("ContainerService.stopContainer called");

        ContainerInfo containerInfo = containerStore.find(project.getId());
        if (containerInfo != null) {
            containerUtil.stopContainer(containerInfo.getContainerId());
            containerStore.remove(project.getId());
        } else {
            throw new BaseException("실행중인 컨테이너가 없습니다.");
        }
    }

    /**
     * 프로젝트의 컨테이너가 실행중인지 확인합니다.
     *
     * @param project
     * @return
     */
    @Override
    public boolean isContainerRunning(Project project) {
        ContainerInfo containerInfo = containerStore.find(project.getId());
        return containerInfo != null && containerInfo.isRunning();
    }
}
