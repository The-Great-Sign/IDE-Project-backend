package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.project.model.ProjectCreateRequestDto;
import goorm.dbjj.ide.domain.project.model.ProjectDto;
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
}
