package goorm.dbjj.ide.domain.project;

import goorm.dbjj.ide.container.ProgrammingLanguage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    private String id;

    private String name;

    private String description;

    private String containerImageId;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage programmingLanguage;

    private String password;

    @ColumnDefault("NOW()")
    private LocalDateTime createdAt;

    // === 세터 == //
    public void setContainerImageId(String containerImageId) {
        this.containerImageId = containerImageId;
    }

}
