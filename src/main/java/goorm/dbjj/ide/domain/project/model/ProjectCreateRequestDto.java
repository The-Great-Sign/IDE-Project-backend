package goorm.dbjj.ide.domain.project.model;

import goorm.dbjj.ide.container.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProjectCreateRequestDto {

    private String name;
    private String description;
    private ProgrammingLanguage programmingLanguage;
    private String password;
}
