package goorm.dbjj.ide.websocket.terminal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class TerminalExecuteRequestDto {
    private String path;
    private String command;
}
