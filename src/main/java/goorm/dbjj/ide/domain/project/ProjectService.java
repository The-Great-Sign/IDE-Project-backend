package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프로젝트의 CRUD를 담당하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ContainerService containerService;

    /**
     * 프로젝트 생성
     * TODO: 추후 비밀번호는 암호화 필요
     *
     * @param requestDto
     * @param userId
     * @return
     */
    @Transactional
    public ProjectDto createProject(ProjectCreateRequestDto requestDto, Long userId) {

        /**
         * TODO: 추후 비밀번호는 암호화 필요
         */
        Project project = Project.createProject(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getProgrammingLanguage(),
                requestDto.getPassword()
        );

        Project savedProject = projectRepository.save(project);

        // 프로젝트 생성 시점에 컨테이너 이미지 생성
        containerService.createProjectImage(project);

        return ProjectDto.of(savedProject);
    }

    /**
     * 프로젝트 비밀번호를 체크합니다.
     * 비밀번호가 맞을 시, DB 작업을 통해 해당 유저가 프로젝트에 참여할 수 있는 권한을 부여합니다.
     *
     * @return 사용자가 프로젝트에 참여 가능하다면 true, 아니라면 false
     */
    @Transactional
    public boolean enter(String requestPassword, String projectId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        // 만약 이미 프로젝트에 참여하고 있다면 true를 반환

        // 프로젝트에 참여하고 있지 않다면, 비밀번호를 체크하고 참여
        if (project.getPassword().equals(requestPassword)) {
            project.addUser(user);
            return true;
        } else {
            return false;
        }
    }


    @Transactional
    public void deleteProject(String projectId, String userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        if(!project.getUser().getId().equals(userId)) {
            throw new BaseException("프로젝트 삭제 권한이 없습니다.");
        }

        /**
         * TODO: 프로젝트 삭제 시, 컨테이너 이미지도 삭제해야 함
         */

        projectRepository.delete(project);
    }
}
