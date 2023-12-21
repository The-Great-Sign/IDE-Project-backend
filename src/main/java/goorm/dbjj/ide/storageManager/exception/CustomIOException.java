package goorm.dbjj.ide.storageManager.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CustomIOException extends Exception{

    private CustomIOExceptionModel model;

    @AllArgsConstructor
    @Getter
    public static class CustomIOExceptionModel {
        private String message;
        private String path;
    }

    public CustomIOException(String message, String path) {
        model = new CustomIOExceptionModel(message, path);
    }
}