package goorm.dbjj.ide.storageManager;

import goorm.dbjj.ide.storageManager.model.Resource;

public interface StorageManager {
    final String RESOURCE_SEPARATOR = "/"; // 분리선

    void saveFile(String path, String fileName, String content);

    Resource loadFile(String path);

    void deleteFile(String path);

    Resource loadDirectory(String path);

    void createDirectory(String path);
}