package goorm.dbjj.ide.domain.fileDirectory;

import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.model.ResourceDto;

import java.util.List;

public interface ProjectFileService {
    void initProjectDirectory(String projectId);

    void createDirectory(String projectId, String path);

    List<ResourceDto> loadProjectDirectory(String projectId);

    void moveFile(String projectId, String oldPath, String newPath);

    void saveFile(String projectId, String filePath, String content);

    FileResponseDto loadFile(String projectId, String filePath);

    void deleteFile(String projectId, String filePath);
}
