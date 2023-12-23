package goorm.dbjj.ide.lambdahandler.executionoutput;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LogicalDirectoryExtractorTest {

    private LogicalDirectoryExtractor extractor = new LogicalDirectoryExtractor();
    @Test
    void extractRoot() {
        String path = "/app";
        String extractedPath = extractor.extract(path);
        assertThat(extractedPath).isEqualTo("/");
    }

    @Test
    void extract() {
        String path = "/app/src/hello.py";
        String extractedPath = extractor.extract(path);
        assertThat(extractedPath).isEqualTo("/src/hello.py");
    }

}