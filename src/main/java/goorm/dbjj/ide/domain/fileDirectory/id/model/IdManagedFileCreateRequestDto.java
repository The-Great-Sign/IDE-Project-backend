package goorm.dbjj.ide.domain.fileDirectory.id.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IdManagedFileCreateRequestDto {

    @NotNull
    private String projectId;

    //it should start with "/"
    @Pattern(regexp = "^/.*", message = "Path는 /로 시작해야 합니다.")
    @NotNull(message = "Path에 null이 들어오면 안됩니다.")
    private String path;
}
