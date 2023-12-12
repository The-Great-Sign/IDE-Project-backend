package goorm.dbjj.ide.domain.user.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 상속받는 자식 클래스까지만 인스턴스 생성 가능.
@Builder
@Table(name = "Users") // 예약어 충돌
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // 이메일
    private String nickname; // 닉네임
    private String imageUrl; // 프로필 이미지

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // GOOGLE

    private String socialId; // 소셜로그인한 id
    private String refreshToken; // 리프레시 토큰

    @CreationTimestamp // INSERT 시 자동으로 값을 채워줌
    private LocalDateTime createdAt;

    @UpdateTimestamp // UPDATE 시 자동으로 값을 채워줌.
    private LocalDateTime updatedAt;

    // 유저 권한 설정 메서드
    public void authorizedUser(){
        this.role = Role.USER;
    }

    // 유저 필드 업데이트
    public void updateNickname(String updateNickname){
        this.nickname = updateNickname;
        updateTime();
    }

    public void updateRefreshToken(String updateRefreshToken){
        this.refreshToken = updateRefreshToken;
        updateTime();
    }

    public void updateTime(){
        this.updatedAt = LocalDateTime.now();
    }

}
