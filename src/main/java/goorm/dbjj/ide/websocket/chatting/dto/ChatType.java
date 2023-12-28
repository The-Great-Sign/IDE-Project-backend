package goorm.dbjj.ide.websocket.chatting.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatType {
    ENTER("ENTER"),
    EXIT("EXIT"),
    TALK("TALK");

    private final String value;

}
