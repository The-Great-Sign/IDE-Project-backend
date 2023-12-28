package goorm.dbjj.ide.websocket;

import java.security.Principal;

/**
 * 유저정보(WebSocketUserSessionMapper)의 키 값을 주고 받기위한 값.
 * */
public class StompPrincipal implements Principal {
    private String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
