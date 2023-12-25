package goorm.dbjj.ide.domain.fileDirectory.id.model;

import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathGenerator {

    @Value("${app.efs-root-directory}")
    private String ROOT_DIRECTORY;

    public String getPath(FileMetadata metadata) {
        return ROOT_DIRECTORY + "/" + metadata.getProject().getId() + metadata.getPath();
    }

    public String getPath(String projectId, String path) {
        return ROOT_DIRECTORY + "/" + projectId + path;
    }
}
