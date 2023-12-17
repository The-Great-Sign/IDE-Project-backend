package goorm.dbjj.ide.util.filewatcher;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileWatcherTest {

    FileWatcher fileWatcher = new FileWatcher();

    @Test
    @Disabled
    void watchTest() throws InterruptedException, IOException {
        try {
            fileWatcher.watch("/Users/goorm/Desktop/test");
        } catch (Exception e) {
            e.printStackTrace();
        }


        File file = new File("/Users/goorm/Desktop/test/app/p1/test.txt");
        file.createNewFile();

        Thread.sleep(2000);
        file.delete();
    }
}