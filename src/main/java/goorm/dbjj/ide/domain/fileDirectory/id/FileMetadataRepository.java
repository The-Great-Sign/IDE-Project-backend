package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.domain.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByProject(Project project);

    Optional<FileMetadata> findByProjectIdAndPath(String projectId, String path);
}
