package goorm.dbjj.ide.container.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CommandStringBuilderTest {

    @Test
    void createCommand() {
        // given
        CommandStringBuilder commandStringBuilder = new CommandStringBuilder();
        String path = "/home/goorm";
        String command = "ls -al";

        // when
        String result = commandStringBuilder.createCommand(path, command);

        // then
        assertThat(result).isEqualTo("bash -c 'cd /home/goorm && pwd && ls -al'");
    }
}