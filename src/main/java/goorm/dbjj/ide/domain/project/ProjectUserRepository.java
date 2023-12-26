package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.project.model.ProjectUser;
import goorm.dbjj.ide.domain.user.dto.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    boolean existsByProjectAndUser(Project project, User user);
}
