package goorm.dbjj.ide.storageManager;

import goorm.dbjj.ide.storageManager.exception.CustomIOException;
import goorm.dbjj.ide.storageManager.model.Resource;

public interface StorageManager {
    final String RESOURCE_SEPARATOR = "/"; // 분리선

    void saveFile(String filePath, String content) throws CustomIOException;

    Resource loadFile(String path) throws CustomIOException;

    void deleteFile(String path) throws CustomIOException;

    Resource loadDirectory(String path) throws CustomIOException;

    void createDirectory(String path) throws CustomIOException;
}