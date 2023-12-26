package goorm.dbjj.ide.domain.project.model;

import goorm.dbjj.ide.container.ProgrammingLanguage;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadata;
import goorm.dbjj.ide.domain.user.dto.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Projects")
@Getter
@NoArgsConstructor
public class Project {

    @Id
    private String id;

    private String name;

    private String description;

    private String containerImageId;

    private String accessPointId;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage programmingLanguage;

    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    //User가 삭제될 때 ProjectUser도 삭제되도록 설정
    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<FileMetadata> fileMetadataList;

    // === 생성 팩토리 메서드 === //
    public static Project createProject(String name, String description, ProgrammingLanguage programmingLanguage, String password, User creator) {
        Project project = new Project();
        project.id = UUID.randomUUID().toString();
        project.name = name;
        project.description = description;
        project.programmingLanguage = programmingLanguage;
        project.password = password;
        project.creator = creator;
        return project;
    }

    // === 세터 == //
    public void setContainerImageId(String containerImageId) {
        this.containerImageId = containerImageId;
    }

    public void setAccessPointId(String accessPointId) {
        this.accessPointId = accessPointId;
    }
}
