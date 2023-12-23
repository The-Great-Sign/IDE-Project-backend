package goorm.dbjj.ide.domain.fileDirectory;

import goorm.dbjj.ide.api.exception.BaseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * 파일의 ID를 생성하는 클래스입니다.
 * 파일의 생성 시점을 기준으로 ID를 생성합니다.
 * 생성 시점이 중복되면 ID도 중복됩니다.
 */
public class FileIdGenerator {

    private FileIdGenerator() {
    }
    public static long generate(File file) {
        Path path = file.toPath();

        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime fileTime = attributes.creationTime();
            return fileTime.toMillis();

        } catch (IOException e) {
            throw new BaseException("파일의 속성을 읽는데 실패했습니다.");
        }
    }
}
