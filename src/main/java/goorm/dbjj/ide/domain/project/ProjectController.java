package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
import goorm.dbjj.ide.domain.project.model.ProjectJoinRequestDto;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * @param projectCreateRequestDto
     * @param user
     * @return
     */
    @PostMapping
    public ApiResponse<ProjectDto> createProject(
            @RequestBody ProjectCreateRequestDto projectCreateRequestDto,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(projectService.createProject(projectCreateRequestDto, user));
    }

    /**
     * 프로젝트를 삭제합니다.
     * 로그인이 되어있어야 합니다.
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

        projectService.deleteProject(projectId, user);
        return ApiResponse.ok();
    }

    /**
     * 프로젝트에 참가합니다.
     * 비밀번호 입력 시 호출되는 API의 엔드포인트입니다.
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

        projectService.join(
                projectJoinRequestDto.getPassword(),
                projectJoinRequestDto.getProjectId(),
                user
        );

        return ApiResponse.ok();
    }
}
