package goorm.dbjj.ide.websocket.terminal;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.websocket.terminal.dto.TerminalExecuteRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerminalService {
    private final ContainerService containerService;
    private final ProjectRepository projectRepository;

    /**
     * Terminal 실행 명령어
     * */
    public void executeTerminal(TerminalExecuteRequestDto terminalExecuteRequestDto, String projectId, Long userId) {
        // 프로젝트 조회하는 로직
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if(projectOptional.isEmpty()){
            log.error("프로젝트가 없습니다!");
            throw new BaseException("프로젝트가 존재하지 않습니다");
        }

        containerService.executeCommand(projectOptional.get(), terminalExecuteRequestDto.getPath(), terminalExecuteRequestDto.getCommand(), userId);
    }
}
