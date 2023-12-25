package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.storageManager.model.ResourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private String path;

    @Enumerated(EnumType.STRING)
    ResourceType type;

    public FileMetadata(Project project, String path, ResourceType type) {
        this.project = project;
        this.path = path;
        this.type = type;
    }

    public String getProjectId() {
        return project.getId();
    }

    public void changePath(String path) {
        this.path = path;
    }
}
