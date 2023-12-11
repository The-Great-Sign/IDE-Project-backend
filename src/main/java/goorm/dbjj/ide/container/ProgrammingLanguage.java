package goorm.dbjj.ide.container;

import lombok.Getter;

import java.util.List;


@Getter
public enum ProgrammingLanguage {
    PYTHON("python:3", List.of("python","-m","http.server","8000"));

    private final String image;
    private final List<String> command;

    ProgrammingLanguage(String image, List<String> command) {
        this.image = image;
        this.command = command;
    }
}
