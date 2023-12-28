package goorm.dbjj.ide.container;

import lombok.Getter;

import java.util.List;

/**
 * 애플리케이션 내에서 다루는 프로그래밍 언어를 ENUM으로 정의한 클래스입니다.
 */
@Getter
public enum ProgrammingLanguage {
    PYTHON("python:3"),
    JAVA("openjdk:11"),
    CPP("gcc:latest");

    /**
     * Docker Hub에서 사용하는 이미지 이름
     */
    private final String image;

    ProgrammingLanguage(String image) {
        this.image = image;
    }
}
