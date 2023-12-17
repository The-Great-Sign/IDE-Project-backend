package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {

    /**
     * 유저가 생성한 프로젝트 목록 조회
     * @param creator
     * @param pageable
     * @return
     */
    Page<Project> findByCreator(User creator, Pageable pageable);
}
