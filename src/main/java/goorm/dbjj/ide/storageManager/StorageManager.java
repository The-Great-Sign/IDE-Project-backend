package goorm.dbjj.ide.storageManager;

import goorm.dbjj.ide.storageManager.model.Resource;

import java.util.List;

public interface StorageManager {
    final String RESOURCE_SEPARATOR = "/"; // 분리선
//    void saveFile(Resource resource); // save와 create와 병합함
//    void createFile(Resource resource); // save와 create와 병합함

//    void createFile(Resource resource);
void saveFile(String path, String fileName, String content);
    Resource loadFile(String path);

    //    void deleteFile(Resource resource);
    void deleteFile(String path);
    Resource loadDirectory(String path);

    void createDirectory(String path);

}
