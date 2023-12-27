package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.api.ApiResponse;
import goorm.dbjj.ide.domain.user.dto.User;
import goorm.dbjj.ide.domain.user.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Setter @Getter
    @NoArgsConstructor
    static class NicknameUpdateRequest {
        private String nickname;
    }

    // 유저 정보 조회
    @GetMapping("/user/info")
    public ApiResponse<UserDto> getUserInfo(@AuthenticationPrincipal User user){
        log.trace("UserController.getUserInfo() called");
        return ApiResponse.ok(UserDto.of(user));
    }

    // 유저 닉네임 변경
    @PatchMapping("/user/update/nickname")
    public ApiResponse<Void> updateUserNickname(
            @AuthenticationPrincipal User user,
            @RequestBody NicknameUpdateRequest request
     ) {

        log.trace("UserController().updateUserNickname called");

        userService.updateNickname(user, request.getNickname());
        return ApiResponse.ok();
    }
}