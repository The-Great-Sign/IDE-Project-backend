package goorm.dbjj.ide.model.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDeleteRequestDto {
    private String projectId;
    private String path;
}
