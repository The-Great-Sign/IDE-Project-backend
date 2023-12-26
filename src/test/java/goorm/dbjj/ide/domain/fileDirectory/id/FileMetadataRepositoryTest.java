package goorm.dbjj.ide.domain.fileDirectory.id;

import goorm.dbjj.ide.container.ProgrammingLanguage;
import goorm.dbjj.ide.domain.project.ProjectRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.domain.user.UserRepository;
import goorm.dbjj.ide.domain.user.dto.Role;
import goorm.dbjj.ide.domain.user.dto.SocialType;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.storageManager.model.ResourceType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class FileMetadataRepositoryTest {

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByProject_IdAndPath() {

        User user = new User(
                null,
                "testUseremail",
                "testUserNickname",
                "imageUrl",
                "password",
                Role.USER,
                SocialType.GOOGLE,
                "googleId",
                "googleAccessToken",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        userRepository.save(user);

        Project project = Project.createProject(
                "testProjectId",
                "testProjectName",
                ProgrammingLanguage.PYTHON,
                "password",
                user
        );

        projectRepository.save(project);


        FileMetadata fileMetadata = new FileMetadata(project, "/src/hello.py", ResourceType.FILE);
        fileMetadataRepository.save(fileMetadata);

        Optional<FileMetadata> findFileMetadata = fileMetadataRepository.findByProject_IdAndPath(project.getId(), "/src/hello.py");
        assertThat(findFileMetadata.isPresent()).isTrue();
        assertThat(findFileMetadata.get().getPath()).isEqualTo("/src/hello.py");
        assertThat(findFileMetadata.get()).isEqualTo(fileMetadata);
    }
}