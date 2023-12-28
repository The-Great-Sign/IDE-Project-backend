package goorm.dbjj.ide.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ecs.model.AwsVpcConfiguration;
import software.amazon.awssdk.services.ecs.model.LaunchType;
import software.amazon.awssdk.services.ecs.model.NetworkConfiguration;
import software.amazon.awssdk.services.ecs.model.RunTaskRequest;

/**
 * IDE 컨테이너를 실행시키기 위한 RunTaskRequest를 생성하는 클래스입니다.
 */
@Slf4j
@Component
public class RunTaskRequestHelper {

    private final NetworkConfiguration NETWORK_CONFIGURATION;

    public RunTaskRequestHelper(Environment environment) {
        String subnet = environment.getProperty("aws.ecs.subnet", String.class);
        String securityGroup = environment.getProperty("aws.ecs.securityGroup", String.class);
        NETWORK_CONFIGURATION = getNetworkConfiguration(subnet, securityGroup);
    }

    public RunTaskRequest createRunTaskRequest(String taskDefinition) {
        log.trace("RunTaskRequestHelper.createRunTaskRequest called");
        return RunTaskRequest.builder()
                .cluster("IDE_CONTAINER")
                .launchType(LaunchType.FARGATE)
                .taskDefinition(taskDefinition)
                .networkConfiguration(NETWORK_CONFIGURATION)
                .enableExecuteCommand(true)
                .build();
    }

    /**
     * IDE 컨테이너를 실행시키기 위한 NetworkConfiguration을 생성합니다.
     * 시스템 내 하나만 유지시키기 위해 싱글톤으로 구현했습니다.
     * @return
     */
    private NetworkConfiguration getNetworkConfiguration(String subnet, String securityGroup) {
        if(NETWORK_CONFIGURATION != null)
            return NETWORK_CONFIGURATION;

        return NetworkConfiguration.builder()
                .awsvpcConfiguration(
                        AwsVpcConfiguration.builder()
                                .subnets(subnet)
                                .securityGroups(securityGroup)
                                .assignPublicIp("ENABLED")
                                .build()
                )
                .build();
    }
}
