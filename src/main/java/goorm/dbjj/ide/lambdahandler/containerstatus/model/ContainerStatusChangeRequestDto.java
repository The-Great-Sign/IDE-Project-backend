package goorm.dbjj.ide.lambdahandler.containerstatus.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Lambda로부터 전송되는 요청 DTO 입니다.
 */
@ToString
@Getter
@Setter
public class ContainerStatusChangeRequestDto {
    private String taskArn;
    private String taskDefinitionArn;
    private String containerStatus;
}
