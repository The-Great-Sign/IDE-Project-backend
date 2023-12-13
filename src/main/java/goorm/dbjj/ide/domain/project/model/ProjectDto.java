package goorm.dbjj.ide.domain.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Project 정보를 반환하기 위한 응답 DTO
 */
@AllArgsConstructor
@Getter
public class ProjectDto {

    private String id;
    private String name;
    private String description;
    private String programmingLanguage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectDto of(Project project){
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getProgrammingLanguage().toString(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
