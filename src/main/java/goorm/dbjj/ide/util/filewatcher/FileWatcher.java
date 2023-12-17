package goorm.dbjj.ide.util.filewatcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component("myFileWatcher")
public class FileWatcher {

    public void watch(String dir) throws Exception {

        FileAlterationObserver observer = new FileAlterationObserver(dir);

        observer.addListener(new FileAlterationListenerAdaptor() {

            @Override
            public void onDirectoryChange(File directory) {
//                System.out.println("modified directory = " + directory);
            }

            @Override
            public void onDirectoryCreate(File directory) {
                sendToUser(extractProjectId(directory.getPath()));
            }

            @Override
            public void onDirectoryDelete(File directory) {
                System.out.println("delete directory = " + directory);
            }

            @Override
            public void onFileCreate(File file) {
                sendToUser(extractProjectId(file.getPath()));
            }

            @Override
            public void onFileDelete(File file) {
                sendToUser(extractProjectId(file.getPath()));
            }
        });

        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        monitor.addObserver(observer);

        monitor.start();
        log.info("File Watcher Start, dir : {}", dir);
    }

    /**
     * 프로젝트의 파일이 변경되었을 때, 해당 프로젝트를 사용하는 유저들에게 변경사항을 알립니다.
     * WS로 변경됐음을 전송합니다.
     * @param projectId
     */
    private void sendToUser(String projectId) {
        System.out.println("sendToUser = " + projectId);
    }

    private String extractProjectId(String path) {
        String[] split = path.split("/");
        return split[5];
    }
}
