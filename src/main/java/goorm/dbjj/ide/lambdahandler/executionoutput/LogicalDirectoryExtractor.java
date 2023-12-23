package goorm.dbjj.ide.lambdahandler.executionoutput;

import org.springframework.stereotype.Component;

/**
 * 도커의 디렉터리 구조로부터 프로젝트 상대 디렉터리 주소를 추출합니다.
 */
@Component
public class LogicalDirectoryExtractor {

    public String extract(String path) {
        String logicalDirectory = path.substring(path.indexOf("/app")+ 4);
        return logicalDirectory.isEmpty() ? "/" : logicalDirectory;
    }
}
