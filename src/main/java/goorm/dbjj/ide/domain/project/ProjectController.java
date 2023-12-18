package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
import goorm.dbjj.ide.domain.project.model.ProjectJoinRequestDto;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.lambdahandler.containerstatus.model.ContainerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 프로젝트를 생성합니다.
     *
     * @param projectCreateRequestDto
     * @param user
     * @return
     */
    @PostMapping
    public ApiResponse<ProjectDto> createProject(
            @RequestBody ProjectCreateRequestDto projectCreateRequestDto,
            @AuthenticationPrincipal User user
    ) {
        log.debug("ProjectController.createProject called");

        if(user == null) {
            throw new BaseException("로그인이 필요합니다.");
        }

        return ApiResponse.ok(projectService.createProject(projectCreateRequestDto, user));
    }

    /**
     * 프로젝트를 삭제합니다.
     * 로그인이 되어있어야 합니다.
     *
     * @param projectId
     * @param user
     * @return
     */
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> deleteProject(
            @PathVariable String projectId,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectController.deleteProject called");
        log.debug("삭제 요청 : ProjectId = {}, User = {}", projectId, user.getId());

        if(user == null) {
            throw new BaseException("로그인이 필요합니다.");
        }

        projectService.deleteProject(projectId, user);
        return ApiResponse.ok();
    }

    /**
     * 프로젝트에 참가합니다.
     * 비밀번호 입력 시 호출되는 API의 엔드포인트입니다.
     *
     * @param projectJoinRequestDto
     * @param user
     * @return
     */
    @PostMapping("/join")
    public ApiResponse<Void> joinProject(
            @RequestBody ProjectJoinRequestDto projectJoinRequestDto,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectController.runProject called");
        log.debug("참가 요청 : ProjectId = {}, User = {}", projectJoinRequestDto.getProjectId(), user.getId());

        if(user == null) {
            throw new BaseException("로그인이 필요합니다.");
        }

        projectService.join(
                projectJoinRequestDto.getPassword(),
                projectJoinRequestDto.getProjectId(),
                user
        );

        return ApiResponse.ok();
    }

    @PostMapping("/{projectId}/run")
    public ApiResponse<ContainerStatus> runProject(
            @PathVariable String projectId,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectController.runProject called");
        log.debug("프로젝트 컨테이너 실행 요청 : ProjectId = {}, User = {}", projectId, user.getId());

        if(user == null) {
            throw new BaseException("로그인이 필요합니다.");
        }

        ContainerStatus status = projectService.runProject(projectId, user);
        return ApiResponse.ok(status);
    }

    @PostMapping("/{projectId}/stop")
    public ApiResponse<Void> stopProject(
            @PathVariable String projectId
    ) {
        log.trace("ProjectController.stopProject called");
        log.debug("프로젝트 컨테이너 종료 요청 : ProjectId = {}", projectId);

        projectService.stopProject(projectId);
        return ApiResponse.ok();
    }

    /**
     * 유저가 생성한 프로젝트 목록을 조회합니다.
     * @param user
     * @param pageable
     * @return
     */
    @GetMapping("/me/created")
    public ApiResponse<Page<ProjectDto>> getMyCreatedProject(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        log.trace("ProjectController.getMyCreatedProject called");
        log.debug("생성한 프로젝트 조회 요청 : User = {}", user.getId());

        return ApiResponse.ok(projectService.findMyCreatedProject(user, pageable));
    }

    /**
     * 유저가 참여한 프로젝트 목록을 조회합니다.
     * @param user
     * @param pageable
     * @return
     */
    @GetMapping("/me/joined")
    public ApiResponse<Page<ProjectDto>> getMyJoinedProject(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        log.trace("ProjectController.getMyJoinedProject called");
        log.debug("참여한 프로젝트 조회 요청 : User = {}", user.getId());

        return ApiResponse.ok(projectService.findMyJoinedProject(user,pageable));
    }

    /**
     * 개별 프로젝트를 조회합니다.
     * 추후에 validation을 진행해야 합니다.
     * @param projectId
     * @return
     */
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectDto> getProject(
            @PathVariable String projectId
    ) {
        log.trace("ProjectController.getProject called");
        log.debug("프로젝트 단일 조회 요청 : ProjectId = {}",projectId);

        return ApiResponse.ok(projectService.findProjectById(projectId));
    }
}
