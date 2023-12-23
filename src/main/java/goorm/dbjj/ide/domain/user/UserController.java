package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.domain.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    /**
     * 아래 방식처럼 가져와서 사용 가능.
     */
    @GetMapping("/user/me")
    public String profile(@AuthenticationPrincipal User user){
        return user.getNickname()+ "님 안녕하세요!";
    }

    @GetMapping("/user/info")
    public ApiResponse<UserDto> getUserInfo(@AuthenticationPrincipal User user){
        log.trace("UserController.getUserInfo() called");
        return ApiResponse.ok(UserDto.of(user));
    }
}