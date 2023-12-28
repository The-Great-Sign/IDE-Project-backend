package goorm.dbjj.ide.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDeleteRequestDto {
    @NotNull(message = "projectId에는 null 값이 있으면 안됩니다.")
    private String projectId;
    @NotNull(message = "경로에 비어있는 값이 있으면 안됩니다.")
    private String path;
}
