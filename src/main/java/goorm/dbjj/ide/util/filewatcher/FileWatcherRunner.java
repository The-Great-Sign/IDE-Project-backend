package goorm.dbjj.ide.util.filewatcher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component("myFileWatcherRunner")
@RequiredArgsConstructor
public class FileWatcherRunner implements ApplicationRunner {

    @Value("${aws.efs.mountPoint}")
    private String baseDir;

    private final FileWatcher fileWatcher;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        fileWatcher.watch(baseDir);
    }
}
