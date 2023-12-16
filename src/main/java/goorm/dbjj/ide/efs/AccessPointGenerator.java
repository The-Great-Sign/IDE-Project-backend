package goorm.dbjj.ide.efs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.efs.EfsClient;
import software.amazon.awssdk.services.efs.model.CreateAccessPointResponse;

/**
 * EFS Access Point 생성 클래스
 * 프로젝트가 생성되면, EFS 특정 공간을 가리키는 AccessPoint를 만들어야 합니다.
 * EFS AccessPoint는 이미지 빌드시 사용됩니다.
 */
@Component
@RequiredArgsConstructor
public class AccessPointGenerator {

    @Value("${aws.efs.fileSystemId}")
    private String fileSystemId;

    private final EfsClient efsClient;

    public String generateAccessPoint(String projectId) {
        CreateAccessPointResponse response = efsClient.createAccessPoint(req -> req
                        .fileSystemId(fileSystemId)
                        .rootDirectory(req2 -> req2.path("/app/" + projectId))
                        .build()
        );

        return response.accessPointId();
    }
}