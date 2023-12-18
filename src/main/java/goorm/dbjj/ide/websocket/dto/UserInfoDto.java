package goorm.dbjj.ide.websocket.dto;

import goorm.dbjj.ide.domain.user.dto.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserInfoDto {
    private Long id;
    private String email; // 이메일
    private String nickname; // 닉네임

    public UserInfoDto(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}
