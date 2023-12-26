package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.fileDirectory.UserProjectValidator;
import goorm.dbjj.ide.domain.fileDirectory.id.model.IdManagedDirectoryCreateRequestDto;
import goorm.dbjj.ide.domain.fileDirectory.id.model.IdManagedFileCreateRequestDto;
import goorm.dbjj.ide.domain.fileDirectory.id.model.IdManagedFileDeleteRequestDto;
import goorm.dbjj.ide.domain.fileDirectory.id.model.IdManagedFileUpdateRequestDto;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import goorm.dbjj.ide.storageManager.model.ResourceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class IdManagedFileController {

    private final IdManagedProjectFileService projectFileService;
    @PostMapping("/files")
    public ApiResponse<Long> createFile(
            @Validated @RequestBody IdManagedFileCreateRequestDto dto,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.createFile() called : path = {}");
        Long createdId = projectFileService.createFile(
                dto.getProjectId(),
                dto.getPath()
        );

        return ApiResponse.ok(createdId, "파일 생성 성공");
    }

    @PostMapping("/directories")
    public ApiResponse<Long> createDirectory(
            @Validated @RequestBody IdManagedDirectoryCreateRequestDto dto,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.createDirectory() called : path = {}", dto.getPath());
        Long createdId = projectFileService.createDirectory(
                dto.getProjectId(),
                dto.getPath()
        );

        return ApiResponse.ok(createdId, "디렉토리 생성 성공");
    }

    @PutMapping("/files")
    public ApiResponse<String> changeFile(
            @Validated @RequestBody IdManagedFileUpdateRequestDto dto,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.changeFile() called : fileId = {}", dto.getFileId());
        projectFileService.changeFile(
                dto.getFileId(),
                dto.getPath(),
                dto.getContent()
        );

        return ApiResponse.ok("파일 변경 성공");
    }

    @DeleteMapping("/files/{fileId}")
    public ApiResponse<String> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.deleteFile() called : deleteFileId = {}", fileId);
        projectFileService.deleteFile(
                fileId
        );

        return ApiResponse.ok("파일 삭제 성공");
    }

    @DeleteMapping("/directories/{directoryId}")
    public ApiResponse<String> deleteDirectory(
            @PathVariable Long directoryId,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.deleteDirectory() called : deleteDirectoryId = {}", directoryId);
        projectFileService.deleteDirectory(
                directoryId
        );

        return ApiResponse.ok("디렉토리 삭제 성공");
    }

    @GetMapping("/files/{fileId}")
    public ApiResponse<FileResponseDto> getFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.getFile() called : fileId = {}", fileId);
        FileResponseDto dto = projectFileService.loadFile(fileId);
        return ApiResponse.ok(dto);
    }

    @GetMapping("/projects/{projectId}/directory")
    public ApiResponse<List<ResourceDto>> loadProjectDirectory(
            @PathVariable String projectId,
            @AuthenticationPrincipal User user
    ) throws CustomIOException {
        log.trace("IdManagedFileController.loadProjectDirectory() called : projectId = {}", projectId);
        List<ResourceDto> resourceDtos = projectFileService.loadDirectory(projectId);
        return ApiResponse.ok(resourceDtos);
    }
}
