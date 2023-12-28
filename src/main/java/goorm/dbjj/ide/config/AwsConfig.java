package goorm.dbjj.ide.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

@Configuration
@PropertySource("classpath:aws.properties")
public class AwsConfig {

    /**
     * aws.properties에서 가져온 accesskey와 secretkey를 통해 AwsCredentialsProvider를 생성합니다.
     * 이는 인증이 필요한 AWS 서비스에 접근할 때 사용됩니다.
     */
    static class CustomAwsCredentialsProvider implements AwsCredentialsProvider {
        private final String accessKey;
        private final String secretKey;

        public CustomAwsCredentialsProvider(
                String accessKey,
                String secretKey
        ) {
            this.accessKey = accessKey;
            this.secretKey = secretKey;
        }

        @Override
        public AwsCredentials resolveCredentials() {
            return AwsBasicCredentials.create(accessKey, secretKey);
        }
    }

    /**
     * AwsCredentialProvider가 필요한 경우 해당 빈을 주입받아 사용하면 된다.
     * @param accessKey
     * @param secretKey
     * @return
     */
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(
            @Value("${aws.accessKey}") String accessKey,
            @Value("${aws.secretKey}") String secretKey
    ) {
        return new CustomAwsCredentialsProvider(accessKey, secretKey);
    }
}
