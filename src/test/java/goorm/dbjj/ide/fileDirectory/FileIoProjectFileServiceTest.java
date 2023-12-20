package goorm.dbjj.ide.fileDirectory;

import goorm.dbjj.ide.domain.fileDirectory.FileIoProjectFileService;
import goorm.dbjj.ide.model.dto.FileResponseDto;
import goorm.dbjj.ide.storageManager.FileIoStorageManager;
import goorm.dbjj.ide.storageManager.model.ResourceDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Disabled
public class FileIoProjectFileServiceTest {
    FileIoStorageManager fileIoStorageManager = new FileIoStorageManager();
    FileIoProjectFileService fileIoProjectFileService = new FileIoProjectFileService(fileIoStorageManager);

    @Test
    void initProjectDirectory() {
        fileIoProjectFileService.initProjectDirectory("project1");
    }

    @Test
    void createDirectory() {
        fileIoProjectFileService.createDirectory("project1", "/src");
    }

    @Test
    void loadProjectDirectory() {
        List<ResourceDto> r = fileIoProjectFileService.loadProjectDirectory("project2");
        System.out.println(r);
    }

    @Test
    void moveFile() {
        fileIoProjectFileService.moveFile("project2", "/src2/hello.txt", "/src/newFile.txt");
        // projectId : projectId, oldPath : 이동시킬 file, newPath : 이동시킬 directory + 지정할파일명 -> 동일한 파일명이 있을 시에는 덮어씌기
    }

    @Test
    void saveFile() {
        fileIoProjectFileService.saveFile("project1", "/hello", "hello");
    }

    @Test
    void loadFile() {
        FileResponseDto resource = fileIoProjectFileService.loadFile("project2", "/src/hello.txt");
        System.out.println(resource);
    }

    @Test
    void deleteFile() {
        fileIoProjectFileService.deleteFile("project1", "/src");
    }
}
