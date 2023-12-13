package goorm.dbjj.ide.container.command;

import org.springframework.stereotype.Component;

/**
 * 클라이언트에게 받은 명령어를 가공하는 클래스입니다.
 * cd를 통해 사용자의 상태 위치로 이동하고
 * 다음번 path를 제공하기 위해 pwd를 수행하며,
 * 제공된 명령어를 입력합니다.
 */
@Component
public class CommandStringBuilder {

    private final String template = "bash -c 'cd %s && pwd && %s'";

    public String createCommand(String path, String command){
        return String.format(template, path, command);
    }
}
