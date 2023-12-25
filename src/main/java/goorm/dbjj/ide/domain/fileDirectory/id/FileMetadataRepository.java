package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.domain.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByProject(Project project);
}
