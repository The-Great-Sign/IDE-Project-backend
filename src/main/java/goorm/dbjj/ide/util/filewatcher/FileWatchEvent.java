package goorm.dbjj.ide.util.filewatcher;

import goorm.dbjj.ide.storageManager.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 파일 디렉터리의 변경사항을 담는 클래스입니다.
 * 웹소켓으로 사용자에게 전달됩니다.
 */
@Data
@AllArgsConstructor
public class FileWatchEvent {

    private Long fileId;
    private FileWatchEventType event;
    private ResourceType type;
    private String path;
}
