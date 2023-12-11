package goorm.dbjj.ide.container;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ecs.model.*;
import java.util.List;
import java.util.Map;

@Component
public class TaskDefinitionHelper {

    @Value("${aws.efs.fileSystemId}")
    private String fileSystemId;

    private final LogConfiguration LOG_CONFIGURATION = getLogConfiguration();
    private final List<MountPoint> MOUNT_POINT = getMountPoint();


    public RegisterTaskDefinitionRequest createRegisterTaskDefinitionRequest(
            ProgrammingLanguage programmingLanguage,
            String accessPointId
    ) {

        return RegisterTaskDefinitionRequest.builder()
                .containerDefinitions(createContainerDefinition(programmingLanguage))
                .volumes(createVolume(accessPointId, fileSystemId))
                .taskRoleArn("arn:aws:iam::092624380570:role/taskRole")
                .executionRoleArn("arn:aws:iam::092624380570:role/taskExecutionRole")
                .requiresCompatibilities(Compatibility.FARGATE)
                .networkMode(NetworkMode.AWSVPC)
                .cpu("512")
                .memory("1024")
                .family("python")
                .build();
    }

    private Volume createVolume(String accessPointId, String fileSystemId) {
        EFSAuthorizationConfig authorizationConfig = EFSAuthorizationConfig.builder()
                .accessPointId(accessPointId)
                .iam("DISABLED")
                .build();

        EFSVolumeConfiguration efsVolumeConfiguration = EFSVolumeConfiguration.builder()
                .authorizationConfig(authorizationConfig)
                .fileSystemId(fileSystemId)
                .transitEncryption("ENABLED")
                .rootDirectory("/")
                .build();

        return Volume.builder()
                .efsVolumeConfiguration(efsVolumeConfiguration)
                .name("efs")
                .build();
    }

    private ContainerDefinition createContainerDefinition(ProgrammingLanguage programmingLanguage) {
        return ContainerDefinition.builder()
                .logConfiguration(LOG_CONFIGURATION)
                .name("container")
                .image(programmingLanguage.getImage())
                .cpu(512)
                .memory(1024)
                .memoryReservation(1024)
                .essential(true)
                .command(programmingLanguage.getCommand())
                .linuxParameters(linux -> linux.initProcessEnabled(true))
                .mountPoints(MOUNT_POINT)
                .build();
    }



    private List<MountPoint> getMountPoint() {
        if(MOUNT_POINT != null) {
            return MOUNT_POINT;
        }

        return List.of(MountPoint.builder()
                .containerPath("/app")
                .sourceVolume("efs")
                .readOnly(false)
                .build());
    }


    private LogConfiguration getLogConfiguration() {
        if(LOG_CONFIGURATION != null) {
            return LOG_CONFIGURATION;
        }

        return LogConfiguration.builder()
                .logDriver(LogDriver.AWSLOGS)
                .options(Map.of(
                        "awslogs-group", "/ecs/ide",   // CloudWatch Logs 로그 그룹 이름
                        "awslogs-region", "ap-northeast-2",    // 로그 그룹이 위치한 리전
                        "awslogs-stream-prefix", "ecs"    // 로그 스트림의 접두사
                ))
                .build();
    }
}
