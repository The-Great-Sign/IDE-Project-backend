package goorm.dbjj.ide.container;

import lombok.Getter;

import java.util.List;

/**
 * 애플리케이션 내에서 다루는 프로그래밍 언어를 ENUM으로 정의한 클래스입니다.
 */
@Getter
public enum ProgrammingLanguage {
    PYTHON("python:3", List.of("python","-m","http.server","8000"));

    /**
     * Docker Hub에서 사용하는 이미지 이름
     */
    private final String image;

    /**
     * 컨테이너가 실행될 때 실행할 명령어
     */
    private final List<String> command;

    ProgrammingLanguage(String image, List<String> command) {
        this.image = image;
        this.command = command;
    }
}
