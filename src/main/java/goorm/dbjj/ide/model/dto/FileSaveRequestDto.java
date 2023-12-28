package goorm.dbjj.ide.model.dto;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSaveRequestDto {
    private String projectId;
    private String directories;
    private String files;
    private String content;
}
