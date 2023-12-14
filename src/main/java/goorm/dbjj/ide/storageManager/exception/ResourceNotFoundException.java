package goorm.dbjj.ide.storageManager.exception;
import goorm.dbjj.ide.api.exception.BaseException;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
