package goorm.dbjj.ide.test;

import goorm.dbjj.ide.storageManager.StorageManager;
import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequiredArgsConstructor
public class TestController {

    @Value("${app.efs-root-directory}")
    private String ROOT_DIRECTORY;
    private final StorageManager storageManager;

    @GetMapping("/remove")
    public String remove() throws CustomIOException {
        File file = new File(ROOT_DIRECTORY+"/900feca1-b386-4c24-bdbf-8b4aa64c8b24");
        for (File listFile : file.listFiles()) {
            storageManager.deleteFile(listFile.getAbsolutePath());
        }

        return "삭제되었습니다. 파이팅!";
    }
}
