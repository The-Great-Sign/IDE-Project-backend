package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.container.ContainerService;
import goorm.dbjj.ide.domain.fileDirectory.initiator.ProjectDirectoryInitiator;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
import goorm.dbjj.ide.domain.project.model.ProjectUser;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.efs.EfsAccessPointUtil;
import goorm.dbjj.ide.lambdahandler.containerstatus.ContainerStore;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerInfo;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerStatus;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;
    private final EfsAccessPointUtil efsAccessPointUtil;
    private final ContainerStore containerStore;
    private final ProjectDirectoryInitiator projectDirectoryInitiator;

    /**
     * 프로젝트 생성
     * TODO: 추후 비밀번호는 암호화 필요
     *
     * @param requestDto
     * @param userId
     * @return
     */
    @Transactional
    public ProjectDto createProject(ProjectCreateRequestDto requestDto, User creator) throws CustomIOException {

        /**
         * TODO: 추후 비밀번호는 암호화 필요
         */
        Project project = Project.createProject(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getProgrammingLanguage(),
                passwordEncoder.encode(requestDto.getPassword()),
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

        // 프로젝트 디렉터리 생성 및 초기화
        projectDirectoryInitiator.initProjectDirectory(savedProject);

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
            return;
        }

        // 프로젝트에 참여하고 있지 않다면, 비밀번호를 체크하고 참여
//        if (passwordEncoder.matches(requestPassword, project.getPassword())) {
            projectUserRepository.save(new ProjectUser(project, user));
//        } else {
//            throw new BaseException("비밀번호가 일치하지 않습니다.");
//        }
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

    /**
     * 프로젝트를 실행합니다.
     * 컨테이너가 구동중이 아니라면, 구동을 시킨 뒤 PENDING 상태를 반환합니다.
     * @param projectId
     * @param user
     * @return 컨테이너가 이미 구동중이라면, 그 상태를 반환합니다.
     */
    public ContainerStatus runProject(String projectId, User user) {
        User mergedUser = em.merge(user);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        if (!projectUserRepository.existsByProjectAndUser(project, mergedUser)) {
            throw new BaseException("프로젝트에 참여하지 않은 유저입니다.");
        }


        // 컨테이너 마킹
        boolean mark = containerStore.mark(project.getId());

        //만약 첫번째 접근자라면
        if(mark) {
            containerService.runContainer(project);
            return ContainerStatus.PENDING;
        } else { //이후 접근자라면
            return containerStore.find(project.getId()).getStatus();
        }
    }

    /**
     * 프로젝트를 종료합니다.
     * 테스트용으로만 사용하는 메서드입니다. 실제 운영 코드에 x
     * @param projectId
     */
    public void stopProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        containerService.stopContainer(project);
    }

    public Page<ProjectDto> findMyCreatedProject(User user, Pageable pageable) {
        User mergedUser = em.merge(user);

        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createdAt");
        PageRequest req = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(order));

        return projectRepository.findByCreator(mergedUser, req).map(ProjectDto::of);
    }

    public Page<ProjectDto> findMyJoinedProject(User user, Pageable pageable) {
        User mergedUser = em.merge(user);

        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "createdAt");
        PageRequest req = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(order));

        return projectRepository.findByParticipatingUserAndNotCreator(mergedUser, req).map(ProjectDto::of);
    }

    public ProjectDto findProjectById(String projectId) {
        /**
         * 추후에 validation 추가하기
         */
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        return ProjectDto.of(project);
    }

    @Transactional
    public void changePassword(String projectId, String password, User loginedUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("존재하지 않는 프로젝트입니다."));

        //영속성 컨텍스트 문제로 ID를 비교해줍니다.
        if(!project.getCreator().getId().equals(loginedUser.getId())) {
            throw new BaseException("프로젝트 비밀번호 변경 권한이 없습니다.");
        }

        project.changePassword(passwordEncoder.encode(password));
    }
}
