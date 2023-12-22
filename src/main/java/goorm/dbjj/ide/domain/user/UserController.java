package goorm.dbjj.ide.domain.user;

import goorm.dbjj.ide.domain.user.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal User user){

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("id", user.getId());
        responseMap.put("nickname", user.getNickname());
        responseMap.put("email", user.getEmail());
        responseMap.put("image_url", user.getImageUrl());
        responseMap.put("created_at", user.getCreatedAt().toString());

        return ResponseEntity.ok(responseMap);
    }
}