package goorm.dbjj.ide.domain.fileDirectory;

import goorm.dbjj.ide.storageManager.StorageManager;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class FileIdGeneratorTest {

    @Value("${app.efs-root-directory}")
    private String ROOT_DIRECTORY;

    @Autowired
    private StorageManager storageManager;

    @Test
    void generateTest() throws CustomIOException {
        // given
        storageManager.saveFile(ROOT_DIRECTORY+"/test.py","hello");


        File file = new File(ROOT_DIRECTORY+"/test.py");

        // when
        long actual = FileIdGenerator.generate(file);

        // then
        System.out.println("actual = " + actual);

        storageManager.deleteFile(ROOT_DIRECTORY+"/test.py");
    }
}