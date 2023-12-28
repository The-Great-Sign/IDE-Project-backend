package goorm.dbjj.ide.storageManager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null값 무시
@JsonPropertyOrder({"id", "name", "type", "path", "children"})
public class ResourceDto {
    private long id;
    private String name;
    private String type;
    private List<ResourceDto> children;
    private String path;

    /**
     * 파일이라면 null이 되어야한다.
     * 디렉토리라면 null이어서는 안된다. (빈 디렉터리라면 빈 리스트를 가지고 있어야함.
     */
}
