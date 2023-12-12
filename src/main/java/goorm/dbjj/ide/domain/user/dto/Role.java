package goorm.dbjj.ide.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    /**
     * Sprint Security가 내부적으로 권한을 인식하고 처리할 수 있음.
     */
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER");

    private final String key;
}
