package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.domain.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/main")
    public String main(){
        return "로그인 되었습니다!";
    }
    @GetMapping("/jwt-test")
    public String jwtTest(){
        return "jwt Test 요청 성공!";
    }

    @GetMapping("/login-success")
    public String loginSuccess(Principal principal){
        User user = userService.getUser(principal.getName());
        String nickname = user.getNickname();

        return "로그인 성공! 환영합니다 "+ nickname + "님 !";
    }
}