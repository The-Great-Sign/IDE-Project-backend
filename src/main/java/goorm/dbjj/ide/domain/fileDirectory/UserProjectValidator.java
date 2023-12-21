package goorm.dbjj.ide.domain.fileDirectory;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.ProjectUserRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.dto.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User가 Project의 자원에 접근할 수 있는지 검증하는 클래스입니다.
 /Component

 */

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProjectValidator {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final EntityManager em;

    public void validateAccessAuthorizationToProject(User user, String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new BaseException("해당하는 프로젝트가 존재하지 않습니다. ProjectId : "+projectId));

        User mergedUser = em.merge(user);

        if(!projectUserRepository.existsByProjectAndUser(project,mergedUser)) {
             throw new BaseException("프로젝트에 접근할 권한이 없습니다.");
        }
    }
}
