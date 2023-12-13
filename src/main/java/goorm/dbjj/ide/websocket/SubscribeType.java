package goorm.dbjj.ide.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum SubscribeType {
    CHATTING("CHATTING"),
    TERMINAL("TERMINAL"),
    CURSOR("CURSOR");

    private final String value;
}