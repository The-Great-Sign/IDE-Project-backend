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
     * 터미널, 파일디렉터리 등 userId를 단순히 비교만 하는경우에 사용한다.
     * */
    public UserInfoDto(Long id){
        this.id=id;
        this.email=null;
        this.nickname=null;
    }


    /**
    * 웹소켓이 이미 실행되고 있는지 비교하기 위해 생성
     * id 값만 비교 => db 조회 덜하기 위한 조치
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoDto that = (UserInfoDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
