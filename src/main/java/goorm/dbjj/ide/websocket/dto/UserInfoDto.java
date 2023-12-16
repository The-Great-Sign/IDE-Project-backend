package goorm.dbjj.ide.websocket.dto;

import goorm.dbjj.ide.domain.user.dto.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    /**
    * 웹소켓이 이미 실행되고 있는지 비교하기 위해 생성
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoDto that = (UserInfoDto) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, nickname);
    }
}
