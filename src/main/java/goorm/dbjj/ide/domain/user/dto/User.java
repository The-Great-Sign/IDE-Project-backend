package goorm.dbjj.ide.domain.user.dto;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Builder
@Entity
@Table(name = "Users") // 예약어 충돌
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 상속받는 자식 클래스까지만 인스턴스 생성 가능.
@AllArgsConstructor
@Transactional
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email; // 이메일
    private String nickname; // 닉네임
    private String imageUrl; // 프로필 이미지
    private String password; // 필수 field.

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

    // 유저 필드 업데이트
    public void updateNickname(String updateNickname){
        this.nickname = updateNickname;
    }

    public void updateRefreshToken(String updateRefreshToken){
        this.refreshToken = updateRefreshToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.getRole().getKey()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
