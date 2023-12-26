package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, String> {

    /**
     * 유저가 생성한 프로젝트 목록 조회
     * @param creator
     * @param pageable
     * @return
     */
    Page<Project> findByCreator(User creator, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.projectUsers pu WHERE pu.user = :user AND p.creator != :user")
    Page<Project> findByParticipatingUserAndNotCreator(@Param("user") User user, Pageable pageable);
}
