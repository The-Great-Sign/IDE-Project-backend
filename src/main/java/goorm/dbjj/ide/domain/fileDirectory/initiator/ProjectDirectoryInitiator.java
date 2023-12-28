package goorm.dbjj.ide.domain.fileDirectory.initiator;

import goorm.dbjj.ide.container.ProgrammingLanguage;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadata;
import goorm.dbjj.ide.domain.fileDirectory.id.FileMetadataRepository;
import goorm.dbjj.ide.domain.project.model.Project;
import goorm.dbjj.ide.storageManager.StorageManager;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import goorm.dbjj.ide.storageManager.model.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 프로젝트 디렉터리 생성 및 초기화를 담당하는 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectDirectoryInitiator {

    @Value("${app.efs-root-directory}")
    private String baseDir;

    private final StorageManager storageManager;
    private final FileMetadataRepository fileMetadataRepository;

    /**
     * 프로젝트 디렉터리를 생성합니다.
     * @param project 대상 프로젝트
     */
    public void initProjectDirectory(Project project) throws CustomIOException {
        log.trace("initProjectDirectory called");

        //디렉터리 생성
        String path = baseDir + "/" + project.getId();
        storageManager.createDirectory(path);

        //기본 파일 생성
        createDefaultFile(project, path, project.getProgrammingLanguage());
    }

    /**
     * path에 해당하는 디렉터리에 기본 파일을 생성합니다.
     * ProgrammingLanguage에 따라 다른 파일명과 내용을 생성합니다.
     * @param path
     * @param programmingLanguage
     */
    private void createDefaultFile(Project project, String path, ProgrammingLanguage programmingLanguage) throws CustomIOException {

        String fileName = getFileName(programmingLanguage);
        String content = getFileContent(programmingLanguage);


        fileMetadataRepository.save(new FileMetadata(project, "/" + fileName, ResourceType.FILE));
        storageManager.saveFile(path + "/" + fileName, content);

        fileMetadataRepository.save(new FileMetadata(project, "/readme.md", ResourceType.FILE));
        storageManager.saveFile(path + "/readme.md", "대박징조 화이팅");
    }

    private String getFileName(ProgrammingLanguage programmingLanguage) {
        return switch (programmingLanguage) {
            case JAVA -> "Main.java";
            case PYTHON -> "main.py";
            case CPP -> "main.cpp";
        };
    }

    private String getFileContent(ProgrammingLanguage programmingLanguage) {
        return switch (programmingLanguage) {
            case JAVA -> "public class Main {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(\"Hello World!\");\n" +
                    "    }\n" +
                    "}";
            case PYTHON -> "print(\"Hello World!\")";
            case CPP -> "#include <iostream>\n" +
                    "\n" +
                    "int main() {\n" +
                    "    std::cout << \"Hello World!\" << std::endl;\n" +
                    "    return 0;\n" +
                    "}";
        };
    }
}
