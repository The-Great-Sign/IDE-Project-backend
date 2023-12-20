package goorm.dbjj.ide.model.dto;

import lombok.*;

/**
 * 개별 파일 조회에 대한 응답 DTO입니다.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDto {
    private String filePath;
    private String fileName;
    private String content;
}
