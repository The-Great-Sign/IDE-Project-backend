package goorm.dbjj.ide.storageManager;

import goorm.dbjj.ide.storageManager.model.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class FileIoStorageManagerTest {
    @Autowired
    private FileIoStorageManager fileIoStorageManager;

    private String baseDir = "/Users/goorm/Desktop/test"; // 대소문자 구분 없음 // Test == test


    @Test
    void loadFile() {
        Resource resource = fileIoStorageManager.loadFile(baseDir);

        System.out.println(resource);

    }

    @Test
    void saveFile() {
        fileIoStorageManager.saveFile(baseDir + "/test5/test6", "test5.txt", "hello.py"); // 덮어쓰기
    }


    @Test
    void deleteFile() {
        String filePath = baseDir + "/test5/test6";
        fileIoStorageManager.deleteFile(filePath);
    }

    @Test
    void loadDirectory() {
        Resource directory = fileIoStorageManager.loadDirectory(baseDir + "/test");

        System.out.println(directory);
    }

    @Test
    void createDirectory() {
        String filePath = baseDir + "/test5/test4";
        fileIoStorageManager.createDirectory(filePath);
    }
}