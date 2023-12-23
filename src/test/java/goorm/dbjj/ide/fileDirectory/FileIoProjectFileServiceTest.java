package goorm.dbjj.ide.fileDirectory;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.domain.fileDirectory.ProjectFileService;
import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.StorageManager;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class FileIoProjectFileServiceTest {
    
    @Autowired
    private ProjectFileService projectFileService;

    @Value("${app.efs-root-directory}") //Users/goorm/Desktop/test
    private String baseDir;

    @Autowired
    StorageManager storageManager;

    @BeforeEach
    void rollback() {
        log.info("rollback called! baseDir : {}",baseDir);
        try {
            storageManager.deleteFile(baseDir);
        } catch (CustomIOException e) {
            fail("삭제 중 에러 발생");
        }

        File newFile = new File(baseDir);
        newFile.mkdir();

        File p1 = new File(baseDir + "/project1");
        p1.mkdir();

        File p2 = new File(baseDir + "/project2");
        p2.mkdir();

        File d1 = new File(baseDir + "/project2/src");
        d1.mkdir();

        File f2 = new File(baseDir + "/project2/src/hi.py");
        try (FileWriter fileWriter = new FileWriter(f2)) { // 알아서 덮어쓰기함.
            fileWriter.write("hi");
        } catch (IOException e) {
            Assertions.fail("테스트 환경 초기화 도중 파일 에러가 발생했습니다.");
        }

        File p1File1 = new File(p1.getAbsolutePath()+"/hello.py");
        try (FileWriter fileWriter = new FileWriter(p1File1)) { // 알아서 덮어쓰기함.
            fileWriter.write("print('hello world')");
        } catch (IOException e) {
            Assertions.fail("테스트 환경 초기화 도중 파일 에러가 발생했습니다.");
        }
    }

    @Test
    void createDirectoryTest() {

        projectFileService.createDirectory("project1","/src");

        File file = new File(baseDir+"/project1/src");
        File file2 = new File(baseDir+"/src");
        assertThat(file.isDirectory()).isTrue();
        assertThat(file2.isDirectory()).isFalse();
    }

    @Test
    void deleteDirectoryTest() {
        projectFileService.deleteFile("project2", "/src");

        File file = new File(baseDir+"/project2/src");

        assertThat(file.exists()).isFalse();
    }

    @Test
    void createFileTest() {
        projectFileService.saveFile("project1","/hello2.py", "hello");

        File createdFile = new File(baseDir+"/project1/hello2.py");

        assertThat(createdFile.exists()).isTrue();
        assertThat(createdFile.isFile()).isTrue();
        assertThat(createdFile.getName()).isEqualTo("hello2.py");

        String content = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(createdFile))) {
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            content = sb.toString();
        } catch (IOException e) {
            fail("파일 읽기 실패");
        }

        assertThat(content).isEqualTo("hello");
    }

    @Test
    void deletePathBlankest() {
        assertThatThrownBy(
                () -> projectFileService.deleteFile("project1","")
        ).isInstanceOf(BaseException.class).hasMessage("경로는 null 값이거나 비어있을 수 없습니다.");
    }

    @Test
    void deletePathNullTest() {
        assertThatThrownBy(
                () -> projectFileService.deleteFile("project1",null)
        ).isInstanceOf(BaseException.class).hasMessage("경로는 null 값이거나 비어있을 수 없습니다.");
    }

    @Test
    void noFile() {
        assertThatThrownBy (
                () -> projectFileService.loadFile("project1","/hello2.py")
        ).isInstanceOf(BaseException.class).hasMessage("load 실패 Path: /hello2.py");
    }

    @Test
    void noDirectory() {
        assertThatThrownBy(
                () -> projectFileService.loadFile("project2","/hi")
        ).isInstanceOf(BaseException.class).hasMessage("load 실패 Path: /hi");
    }

    @Test
    void loadFile() {
        FileResponseDto dto = projectFileService.loadFile("project1", "/hello.py");

        assertThat(dto.getFileName()).isEqualTo("hello.py");
        assertThat(dto.getFilePath()).isEqualTo("/hello.py");
        assertThat(dto.getContent()).isEqualTo("print('hello world')");
    }

    @Test
    void loadHierarchyFile() {
        FileResponseDto dto = projectFileService.loadFile("project2", "/src/hi.py");

        assertThat(dto.getFileName()).isEqualTo("hi.py");
        assertThat(dto.getFilePath()).isEqualTo("/src/hi.py");
        assertThat(dto.getContent()).isEqualTo("hi");
    }
}
