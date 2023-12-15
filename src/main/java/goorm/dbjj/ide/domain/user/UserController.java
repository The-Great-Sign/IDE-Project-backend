package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.domain.user.dto.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    /**
     * 테스트용 메서드 입니다!
     */
    @GetMapping("/user/main")
    public String main(){
        return "로그인 되었습니다!";
    }

    /**
     * 아래 방식처럼 가져와서 사용 가능.
     */
    @GetMapping("/user/me")
    public String profile(@AuthenticationPrincipal User user){
        return user.getNickname()+ "님 안녕하세요!";
    }
}