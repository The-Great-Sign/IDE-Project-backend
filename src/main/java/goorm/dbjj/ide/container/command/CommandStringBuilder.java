package goorm.dbjj.ide.container.command;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 클라이언트에게 받은 명령어를 가공하는 클래스입니다.
 * cd를 통해 사용자의 상태 위치로 이동하고
 * 다음번 path를 제공하기 위해 pwd를 수행하며,
 * 제공된 명령어를 입력합니다.
 */
@Component
public class CommandStringBuilder {

    @Value("${aws.lambda.secretKey}")
    private String secretKey;

    @Value("${app.outputSeparator}")
    private String separator;

    @Value("${app.outputSendingUrl}")
    private String outputSendingUrl;

    // (bash -c 'python3 hello.py'; echo -e "\n---\n"; pwd) | curl -d @- "http://localhost:8080/api/execution/output?secretKey=123
    private final String TEMPLATE = "(bash -c 'cd /app ; cd .%s ; %s ; echo -e %s ; pwd') |" +
            " curl -d @- \"%s?secretKey=%s&userId=%s&projectId=%s\"";

    public String createCommand(String path, String command, String projectId, Long userId){
        return String.format(TEMPLATE, path, command, separator, outputSendingUrl, secretKey, userId, projectId);
    }
}
