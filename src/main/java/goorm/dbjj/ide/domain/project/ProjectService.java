package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
import goorm.dbjj.ide.domain.project.model.ProjectUser;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.efs.EfsAccessPointUtil;
import jakarta.persistence.EntityManager;
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
    private final ProjectUserRepository projectUserRepository;
    private final ContainerService containerService;
//    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final EfsAccessPointUtil efsAccessPointUtil;

    /**
     * 프로젝트 생성
     * TODO: 추후 비밀번호는 암호화 필요
     *
     * @param requestDto
     * @param userId
     * @return
     */
    @Transactional
    public ProjectDto createProject(ProjectCreateRequestDto requestDto, User creator) {

        /**
         * TODO: 추후 비밀번호는 암호화 필요
         */
        Project project = Project.createProject(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getProgrammingLanguage(),
                requestDto.getPassword(),
                creator
        );

        Project savedProject = projectRepository.save(project);

        // 프로젝트 생성과 함께 creator가 프로젝트에 참여하도록 함
        projectUserRepository.save(new ProjectUser(savedProject, creator));

        //EFS AccessPoint 생성
        savedProject.setAccessPointId(efsAccessPointUtil.generateAccessPoint(savedProject.getId()));

        // 프로젝트 생성 시점에 컨테이너 이미지 생성
        String containerImageId = containerService.createProjectImage(savedProject);
        savedProject.setContainerImageId(containerImageId);

        return ProjectDto.of(savedProject);
    }

    /**
     * 프로젝트 비밀번호를 체크합니다.
     * 비밀번호가 맞을 시, DB 작업을 통해 해당 유저가 프로젝트에 참여할 수 있는 권한을 부여합니다.
     *
     * @return 사용자가 프로젝트에 참여 가능하다면 true, 아니라면 false
     */
    @Transactional
    public void join(String requestPassword, String projectId, User authenticatedUser) {
        User user = userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new BaseException("존재하지 않는 유저입니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        // 만약 이미 프로젝트에 참여하고 있다면 예외 반환
        if (projectUserRepository.existsByProjectAndUser(project, user)) {
            throw new BaseException("이미 프로젝트에 참여하고 있습니다.");
        }

        // 프로젝트에 참여하고 있지 않다면, 비밀번호를 체크하고 참여
        if (project.getPassword().equals(requestPassword)) {
            projectUserRepository.save(new ProjectUser(project, user));
        } else {
            throw new BaseException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void deleteProject(String projectId, User user) {
        // User를 merge하여 영속성 컨텍스트에 포함시킵니다.
        User mergedUser = em.merge(user);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        if(!project.getCreator().equals(mergedUser)) {
            throw new BaseException("프로젝트 삭제 권한이 없습니다.");
        }

        /**
         * TODO: 프로젝트 삭제 시 해야할 일
         * 1. 프로젝트 컨테이너 이미지 삭제
         * 2. 프로젝트 컨테이너 종료
         * 3. 프로젝트 저장공간 삭제 - ProjectFileService 완성되면 추가
         * 4. 프로젝트 DB 삭제
         */

        //예외로 인하여 다음 코드가 수행되지 않는 상황을 방지하기 위해서 try-catch로 감싸줍니다.
        try {
            containerService.deleteProjectImage(project);
        } catch (Exception e) {
            log.error("프로젝트 컨테이너 이미지 삭제 실패 : {}", e.getMessage());
        }

        //예외로 인하여 다음 코드가 수행되지 않는 상황을 방지하기 위해서 try-catch로 감싸줍니다.
        try {
            containerService.stopContainer(project);
        } catch (Exception e) {
            log.debug("프로젝트 컨테이너 종료 실패 : {}", e.getMessage());
        }

        //예외로 인하여 다음 코드가 수행되지 않는 상황을 방지하기 위해서 try-catch로 감싸줍니다.
        try {
            efsAccessPointUtil.deleteAccessPoint(project.getAccessPointId());
        } catch (Exception e) {
            log.debug("프로젝트 EFS AccessPoint 삭제 실패 : {}", e.getMessage());
        }

        projectRepository.delete(project);
    }
}
