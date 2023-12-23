package goorm.dbjj.ide.domain.fileDirectory;


import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.model.dto.FileDeleteRequestDto;
import goorm.dbjj.ide.model.dto.FileLoadRequestDto;
import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.model.dto.FileSaveRequestDto;
import goorm.dbjj.ide.storageManager.model.ResourceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProjectFileController {

    private final FileIoProjectFileService fileIoProjectFileService;
    private final UserProjectValidator userProjectValidator;

    @GetMapping("/api/files") // 특정파일 조회
    public ApiResponse<FileResponseDto> loadFile(
            @ModelAttribute FileLoadRequestDto fileLoadRequestDto,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectFileController.loadFile 실행");

        userProjectValidator.validateAccessAuthorizationToProject(
                user,
                fileLoadRequestDto.getProjectId()
        );

        FileResponseDto file = fileIoProjectFileService.loadFile(
                fileLoadRequestDto.getProjectId(),
                fileLoadRequestDto.getFilePath()
        );

        FileResponseDto responseDto = FileResponseDto.builder()
                .id(file.getId())
                .filePath(file.getFilePath())
                .fileName(file.getFileName())
                .content(file.getContent()).build();

        return ApiResponse.ok(responseDto, "파일 로드 성공");
    }

    @PostMapping("/api/files")
    public ApiResponse<FileResponseDto> saveFile(
            @RequestBody FileSaveRequestDto fileSaveRequestDto,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectFileController.saveFile 실행");

        userProjectValidator.validateAccessAuthorizationToProject(
                user,
                fileSaveRequestDto.getProjectId()
        );


        if (fileSaveRequestDto.getDirectories() != null) {
            fileIoProjectFileService.createDirectory(
                    fileSaveRequestDto.getProjectId(),
                    fileSaveRequestDto.getDirectories()
            );
        } else {
            fileIoProjectFileService.saveFile(
                    fileSaveRequestDto.getProjectId().toString(),
                    fileSaveRequestDto.getFiles(),
                    fileSaveRequestDto.getContent()
            );
        }
        return ApiResponse.ok(null, "성공적으로 저장했습니다.");
    }

    @DeleteMapping("/api/files")
    public ApiResponse<Void> deleteFile(
           @Valid @RequestBody FileDeleteRequestDto fileDeleteRequestDto,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectFileController.deleteFile 실행");

        userProjectValidator.validateAccessAuthorizationToProject(
                user,
                fileDeleteRequestDto.getProjectId()
        );

        fileIoProjectFileService.deleteFile(fileDeleteRequestDto.getProjectId(), fileDeleteRequestDto.getPath());
        return ApiResponse.ok(null, "삭제했습니다");
    }

    @GetMapping("/api/projects/{projectId}/directory")
    public ApiResponse<List<ResourceDto>> loadProjectDirectory(
            @PathVariable(name = "projectId") String projectId,
            @AuthenticationPrincipal User user
    ) {
        log.trace("ProjectFileController.loadProjectDirectoryFile 실행");

        userProjectValidator.validateAccessAuthorizationToProject(
                user,
                projectId
        );

        List<ResourceDto> directory = fileIoProjectFileService.loadProjectDirectory(projectId);
        return ApiResponse.ok(directory);
    }
}
