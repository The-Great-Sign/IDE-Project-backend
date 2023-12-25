package goorm.dbjj.ide.domain.fileDirectory.id.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdManagedDirectoryDeleteRequestDto {

    @NotNull
    private Long directoryId;
}
