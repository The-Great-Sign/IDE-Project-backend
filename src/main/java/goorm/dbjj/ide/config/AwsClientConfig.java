package goorm.dbjj.ide.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.efs.EfsClient;

/**
 * AWS Client 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class AwsClientConfig {

    private final AwsCredentialsProvider awsCredentialsProvider;

    @Bean
    public EcsClient ecsClient() {
        return EcsClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    @Bean
    public EfsClient efsClient() {
        return EfsClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}
