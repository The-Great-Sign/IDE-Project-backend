package goorm.dbjj.ide.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserDto {
    private Long id;
    private String nickname;
    private String email;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static UserDto of(User user){
        return new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getImageUrl(),
                user.getCreatedAt());
    }
}
