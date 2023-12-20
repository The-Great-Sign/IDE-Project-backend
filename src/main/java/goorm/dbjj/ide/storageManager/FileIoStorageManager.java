package goorm.dbjj.ide.storageManager;

import goorm.dbjj.ide.api.exception.BaseException;
import goorm.dbjj.ide.storageManager.exception.ResourceNotFoundException;
import goorm.dbjj.ide.storageManager.model.Resource;
import goorm.dbjj.ide.storageManager.model.ResourceType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static goorm.dbjj.ide.storageManager.model.ResourceType.DIRECTORY;

@Component
public class FileIoStorageManager implements StorageManager {

    @Override
    public void saveFile(String filePath, String content) { // saveFile + CreateFile 병합함 -> 덮어씌우기 형식으로 변환
        File file = new File(filePath); // 파일 경로, 이름 합침
        try (FileWriter fileWriter = new FileWriter(file)) { // 알아서 덮어쓰기함.
            fileWriter.write(content); // 파일에 content 작성
//                fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException("파일 처리중 오류 발생");
        }
    }

    @Override
    public Resource loadFile(String path) {
        File file = new File(path);

        String fileName = file.getName();
        String filePath = file.getPath();
        String content = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            content = sb.toString();
        } catch (IOException e) {
            throw new BaseException("파일 load 실패 : " + filePath);
        }
        return Resource.file(fileName, filePath, content);
    }

    @Override
    public void deleteFile(String path) { // delete -> 파일 디렉토리 동일 처리(병합)
        File file = new File(path);

        // 하위 파일 및 폴더 먼저 삭제
        if (file.isDirectory()) {
            String[] subFiles = file.list();
            for (String sub : subFiles) {
                deleteFile(path + File.separator + sub);
            }
        }
        if (!file.delete()) {
            throw new BaseException("파일 삭제 실패 :  " + path + "에 폴더나 파일이 존재하지 않습니다.");
        }
    }

    @Override
    public void createDirectory(String path) {
        File file = new File(path);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            throw new BaseException("상위 디렉터리가 존재하지 않습니다. : " + parentDir.getAbsolutePath());
        }
        if (!file.mkdir()) { // .mkdir() => 필요한 상위폴더 생성 X -> exception , .mkdirs() => 필요한 상위폴더 생성
            throw new BaseException("Directory 생성 실패 : " + path);
            /**
             * TODO : throw 에뤌
             */
        }
    }

    @Override
    public Resource loadDirectory(String path) { // 경로, resourceType + fileName,
        File file = new File(path);

        if (!file.exists()) {
            throw new ResourceNotFoundException("디렉토리가 존재하지 않습니다 : " + path);
        }
        if (!file.isDirectory()) {
            throw new ResourceNotFoundException("디렉토리 경로가 아닙니다 : " + path);
        }

        Resource resource = Resource.directory(file.getName(), file.getAbsolutePath());
        resource.setChild(loadDirectoryRecursive(file));
        return resource;
    }

    private List<Resource> loadDirectoryRecursive(File directory) {
        List<Resource> res = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fullPath = file.getAbsolutePath();
                Resource resource = Resource.resource(file.getName(), fullPath, file.isFile() ? ResourceType.FILE : DIRECTORY);
                if (file.isDirectory()) {
                    List<Resource> child = loadDirectoryRecursive(file);
                    resource.setChild(child);
                }
                res.add(resource);
            }
        }
        return res;
    }
}