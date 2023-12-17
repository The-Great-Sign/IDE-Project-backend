package goorm.dbjj.ide.util.filewatcher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * FileWatcherRunner
 *
 * FileWatcher를 애플리케이션 로딩이 끝난 시점에 수행시키는 역할을 합니다.
 * 파일을 감지하는 위치는 aws.efs.mountPoint에서 정의합니다.
 */
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
