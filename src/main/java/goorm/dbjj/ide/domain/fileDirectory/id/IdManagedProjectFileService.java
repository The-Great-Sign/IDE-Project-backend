package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.fileDirectory.id.model.PathGenerator;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.StorageManager;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import goorm.dbjj.ide.storageManager.model.Resource;
import goorm.dbjj.ide.storageManager.model.ResourceDto;
import goorm.dbjj.ide.storageManager.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static goorm.dbjj.ide.storageManager.StorageManager.RESOURCE_SEPARATOR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IdManagedProjectFileService {

    private final StorageManager storageManager;
    private final FileMetadataRepository fileMetadataRepository;
    private final ProjectRepository projectRepository;
    private final PathGenerator pathGenerator;

    @Value("${app.efs-root-directory}")
    private String ROOT_DIRECTORY;

    private String getRelativePath(String fullPath) {
        if (fullPath != null && fullPath.startsWith(ROOT_DIRECTORY)) {
            String withoutRoot = fullPath.substring(ROOT_DIRECTORY.length());
            return withoutRoot.startsWith(RESOURCE_SEPARATOR) ? // root 가 '/' 로 시작하는지 확인
                    withoutRoot.substring(withoutRoot.indexOf(RESOURCE_SEPARATOR, 1)) : withoutRoot;
        }
        return fullPath;
    }

    //파일을 생성한다.
    //파일이 이미 존재하는가? -> 직접 검증
    //파일을 생성할 수 있는 위치인가? -> saveFile에서 검증
    public void createFile(String projectId, String path) throws CustomIOException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("해당 프로젝트가 존재하지 않습니다."));

        if (storageManager.exists(pathGenerator.getPath(projectId, path))) {
            throw new BaseException("해당 위치에 파일이 이미 존재합니다.");
        }


        storageManager.saveFile(pathGenerator.getPath(projectId, path), "");
    }


    /**
     * 디렉터리를 생성합니다.
     * <p>
     * 디렉터리가 이미 존재하나?
     * 디렉터리를 생성할 수 있는 위치인가?
     *
     * @param projectId
     * @param path
     * @return
     */
    public void createDirectory(String projectId, String path) throws CustomIOException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("해당 프로젝트가 존재하지 않습니다."));

        if (storageManager.exists(ROOT_DIRECTORY + "/" + projectId + path)) {
            throw new BaseException("해당 위치에 파일이 이미 존재합니다.");
        }

        storageManager.createDirectory(pathGenerator.getPath(project.getId(), path));
    }

    public List<ResourceDto> loadDirectory(String projectId) throws CustomIOException {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BaseException("해당 프로젝트가 존재하지 않습니다."));

        List<FileMetadata> fileMetadatas = fileMetadataRepository.findByProject(project);

        Resource resource = storageManager.loadDirectory(ROOT_DIRECTORY + "/" + projectId);

        return convertResourceListToDtoList(resource.getChildren(), ROOT_DIRECTORY + "/" + projectId, fileMetadatas);
    }

    /**
     * 파일을 수정합니다.
     * <p>
     * 수정할 파일이 존재하는가?
     * 수정할 파일이 디렉터리인가?
     * 수정될 위치에 파일이 존재하는가?
     *
     * @param fileId
     * @param path
     * @param content
     */
    public void changeFile(Long fileId, String content) throws CustomIOException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new BaseException("해당 파일이 존재하지 않습니다.:FileMetadata"));

        File originFile = new File(pathGenerator.getPath(fileMetadata));
        if (!originFile.exists()) {
            throw new BaseException("삭제하려는 파일이 존재하지 않습니다.");
        }

        if (fileMetadata.getType().equals(ResourceType.DIRECTORY)) {
            throw new BaseException("디렉토리를 변경하려고 합니다.");
        }

        storageManager.saveFile(
                pathGenerator.getPath(
                        fileMetadata.getProjectId(),
                        fileMetadata.getPath()
                ),
                content
        );
    }

    /**
     * 파일을 삭제합니다.
     * <p>
     * 삭제할 파일이 존재하는가?
     * 삭제할 파일이 디렉터리인가?
     *
     * @param fileId
     */
    public void deleteFile(Long fileId) throws CustomIOException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new BaseException("해당 파일이 존재하지 않습니다."));

        File originFile = new File(pathGenerator.getPath(fileMetadata));
        if (!originFile.exists()) {
            throw new BaseException("삭제하려는 파일이 존재하지 않습니다.");
        }

        if (fileMetadata.getType().equals(ResourceType.DIRECTORY)) {
            throw new BaseException("디렉토리를 삭제하려고 합니다.");
        }

        storageManager.deleteFile(pathGenerator.getPath(fileMetadata));
    }

    /**
     * 디렉터리를 삭제합니다.
     * <p>
     * 삭제할 디렉터리가 존재하는가?
     * 삭제할 디렉터리가 파일인가?
     *
     * @param fileId
     */
    public void deleteDirectory(Long fileId) throws CustomIOException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new BaseException("해당 폴더가 존재하지 않습니다."));

        File originFile = new File(pathGenerator.getPath(fileMetadata));
        if (!originFile.exists()) {
            throw new BaseException("삭제하려는 폴더가 존재하지 않습니다.");
        }

        if (fileMetadata.getType().equals(ResourceType.FILE)) {
            throw new BaseException("파일을 삭제하려고 합니다.");
        }

        storageManager.deleteFile(pathGenerator.getPath(fileMetadata));
    }

    public FileResponseDto loadFile(Long fileId) throws CustomIOException {

        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new BaseException("해당 파일이 존재하지 않습니다."));

        if (fileMetadata.getType().equals(ResourceType.DIRECTORY)) {
            throw new BaseException("디렉토리를 불러오려고 합니다.");
        }

        Resource resource = storageManager.loadFile(ROOT_DIRECTORY + "/" + fileMetadata.getProject().getId() + fileMetadata.getPath());
        return new FileResponseDto(
                fileMetadata.getId(),
                fileMetadata.getPath(),
                resource.getName(),
                resource.getContent()
        );
    }

    private List<ResourceDto> convertResourceListToDtoList(
            List<Resource> resources,
            String parentPath,
            List<FileMetadata> metadatas
    ) {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        if (resources != null) {
            for (Resource resource : resources) {
                String resourceFullPath = parentPath + RESOURCE_SEPARATOR + resource.getName();
                resourceDtos.add(convertResourceToResourceDto(resource, resourceFullPath, metadatas));
            }
        }
        return resourceDtos;
    }

    private ResourceDto convertResourceToResourceDto(Resource resource, String
            fullPath, List<FileMetadata> metadatas) {
        List<ResourceDto> childDtos = null;
        String relationPath = getRelativePath(fullPath);

        if (resource.isDirectory()) {
            childDtos = new ArrayList<>();
            if (resource.getChildren() != null) {
                for (Resource child : resource.getChildren()) {
                    String childFullPath = fullPath + RESOURCE_SEPARATOR + child.getName();
                    String childRelativePath = getRelativePath(fullPath);
                    childDtos.add(convertResourceToResourceDto(child, childFullPath, metadatas));
                }
            }
        }
        String relativePath = getRelativePath(fullPath);

        Long id = metadatas.stream().filter(metadata -> metadata.getPath().equals(relativePath)).findFirst().get().getId();

        return ResourceDto.builder()
                .id(id)
                .name(resource.getName())
                .type(resource.getResourceType().toString())
                .children(childDtos)
                .path(relativePath)
                .build();
    }


}
