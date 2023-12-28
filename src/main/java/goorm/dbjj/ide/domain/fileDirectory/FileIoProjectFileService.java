package goorm.dbjj.ide.domain.fileDirectory;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.FileIoStorageManager;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import goorm.dbjj.ide.storageManager.model.Resource;
import goorm.dbjj.ide.storageManager.model.ResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static goorm.dbjj.ide.storageManager.StorageManager.RESOURCE_SEPARATOR;

@Slf4j
@Component
public class FileIoProjectFileService implements ProjectFileService {

    @Value("${app.efs-root-directory}")
    private String ROOT_DIRECTORY;

    private String getFullPath(String projectId, String subPath) {

        validateSubPath(subPath);

        String fullPath = ROOT_DIRECTORY + RESOURCE_SEPARATOR + projectId + subPath;
        log.trace("전체 경로 = {}", fullPath);

        return fullPath;
    }

    private void validateSubPath(String subPath) {
        if (subPath == null || subPath.trim().isEmpty()) {
            throw new BaseException("subPath가 null값이거나 비어있을 수 없습니다.");
        }
        if (!subPath.startsWith(RESOURCE_SEPARATOR)) {
            throw new BaseException("subPath는 반드시 '/'로 시작해야 합니다.");
        }
        if (subPath.contains("/../")) {
            throw new BaseException("잘못된 subPath 입력 또는 상위 Directory로 접근할 수 없습니다");
        }
    }

    private String getRelativePath(String fullPath) {
        if (fullPath != null && fullPath.startsWith(ROOT_DIRECTORY)) {
            String withoutRoot = fullPath.substring(ROOT_DIRECTORY.length());
            log.trace("클라이언트 전달 상대경로 = {}", withoutRoot.startsWith(RESOURCE_SEPARATOR) ? // root 가 '/' 로 시작하는지 확인
                    withoutRoot.substring(withoutRoot.indexOf(RESOURCE_SEPARATOR, 1)) : withoutRoot);
            return withoutRoot.startsWith(RESOURCE_SEPARATOR) ? // root 가 '/' 로 시작하는지 확인
                    withoutRoot.substring(withoutRoot.indexOf(RESOURCE_SEPARATOR, 1)) : withoutRoot;
        }
        return fullPath;
    }

    private final FileIoStorageManager storageManager;

    public FileIoProjectFileService(FileIoStorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public void initProjectDirectory(String projectId) {
        log.trace("Service.initProjectDirectory - 첫 로그인 시 프로젝트 디렉토리 생성");
        String directoryPath = getFullPath(projectId, "");

        try {
            storageManager.createDirectory(directoryPath);
        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }

    }

    @Override
    public void createDirectory(String projectId, String path) {
        log.trace("Service.createDirectory - 디렉토리 생성");
        String fullPath = getFullPath(projectId, path);

        try {
            storageManager.createDirectory(fullPath);
        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }
    }

    @Override
    public List<ResourceDto> loadProjectDirectory(String projectId) {
        log.trace("Service.loadProjectDirectory - 디렉토리 구조 조회");
        String directoryPath = buildFullPath(projectId);
        Resource directory = loadDirectory(directoryPath);
        return convertResourceListToDtoList(directory.getChildren(), directoryPath);
    }

    private String buildFullPath(String projectId) {
        return ROOT_DIRECTORY + RESOURCE_SEPARATOR + projectId;
    }

    private Resource loadDirectory(String fullPath) {
        try {
            return storageManager.loadDirectory(fullPath);
        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }
    }

    private List<ResourceDto> convertResourceListToDtoList(List<Resource> resources, String parentPath) {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        if (resources != null) {
            for (Resource resource : resources) {
                String resourceFullPath = parentPath + RESOURCE_SEPARATOR + resource.getName();
                resourceDtos.add(convertResourceToResourceDto(resource, resourceFullPath));
            }
        }
        return resourceDtos;
    }

    private ResourceDto convertResourceToResourceDto(Resource resource, String fullPath) {
        List<ResourceDto> childDtos = null;
        String relationPath = getRelativePath(fullPath);

        if (resource.isDirectory()) {
            childDtos = new ArrayList<>();
            if (resource.getChildren() != null) {
                for (Resource child : resource.getChildren()) {
                    String childFullPath = fullPath + RESOURCE_SEPARATOR + child.getName();
                    String childRelativePath = getRelativePath(fullPath);
                    childDtos.add(convertResourceToResourceDto(child, childFullPath));
                }
            }
        }
        String relativePath = getRelativePath(fullPath);

        return ResourceDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getResourceType().toString())
                .children(childDtos)
                .path(relativePath)
                .build();
    }

    @Override
    public void moveFile(String projectId, String oldPath, String newPath) { // 생성과 삭제 조합 -> oldPath에서 삭제하고 newPath에서 다시 생성 => File 단위 O, Directory 단위 X

        log.trace("Service.moveFile - 파일 이동");

        String fileOldPath = getFullPath(projectId, oldPath);
        String fileNewPath = getFullPath(projectId, newPath);

        try {
            Resource resource = storageManager.loadFile(fileOldPath);
            storageManager.saveFile(fileNewPath, resource.getContent());
            storageManager.deleteFile(fileOldPath);
        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }
    }


    @Override
    public void saveFile(String projectId, String filePath, String content) {
        log.trace("Service.saveFile - 파일 수정 및 저장");
        String fullPath = getFullPath(projectId, filePath);
        try {
            storageManager.saveFile(fullPath, content);
        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }
    }

    @Override
    public FileResponseDto loadFile(String projectId, String filePath) { // 재귀로 로드 // 관심사 분리
        log.trace("Service.loadFile - 파일 조회");
        String fullPath = getFullPath(projectId, filePath);

        try {
            Resource resource = storageManager.loadFile(fullPath);
            String relativePath = getRelativePath(fullPath);
            return toFileResponseDto(resource, relativePath);

        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }
    }


    @Override
    public void deleteFile(String projectId, String filePath) {
        log.trace("Service.deleteFile - 파일 및 디렉토리 삭제");

        if (projectId == null || projectId.trim().isEmpty()) {
            throw new BaseException("projectId 는 null 값이거나 빈값이 들어올 수 없습니다.");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            log.trace("경로 값 오류발생 - path값 재확인이 필요합니다. ");
            throw new BaseException("경로는 null 값이거나 비어있을 수 없습니다.");
        }

        String fullPath = getFullPath(projectId, filePath);

        try {
            storageManager.deleteFile(fullPath);
        } catch (CustomIOException e) {
            String relativePath = getRelativePath(e.getModel().getPath());
            throw new BaseException(e.getModel().getMessage() + " Path: " + relativePath);
        }
    }

    private FileResponseDto toFileResponseDto(Resource resource, String relativePath) {

        return FileResponseDto.builder()
                .id(resource.getId())
                .fileName(resource.getName())
                .content(resource.getContent())
                .filePath(relativePath)
                .build();
    }
}