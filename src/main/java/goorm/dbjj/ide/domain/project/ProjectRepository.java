package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.domain.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
