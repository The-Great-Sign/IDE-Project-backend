package goorm.dbjj.ide.auth.oauth2;

import goorm.dbjj.ide.auth.oauth2.info.GoogleOAuth2UserInfo;
import goorm.dbjj.ide.auth.oauth2.info.KakaoOAuth2UserInfo;
import goorm.dbjj.ide.auth.oauth2.info.OAuth2UserInfo;
import goorm.dbjj.ide.domain.user.dto.Role;
import goorm.dbjj.ide.domain.user.dto.SocialType;
import goorm.dbjj.ide.domain.user.dto.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * 소셜(Google)에서 받는 데이터를 분기 처리 하는 DTO 클래스
 * 이후, 다른 플랫폼을 추가하게 될지도 모른다는 가능성을 두고, 클래스 생성.
 */
@Getter
public class OAuthAttributes {
    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

    @Builder
    private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    /**
     * socialType에 맞는 메서드 호출 -> OAuthAttributes 객체 반환.
     */
    public static OAuthAttributes of(
            SocialType socialType,
            String userNameAttributeName,
            Map<String, Object> attributes
    ){
        if(socialType == SocialType.GOOGLE){
            return ofGoogle(userNameAttributeName, attributes);
        }
        if(socialType == SocialType.KAKAO){
            return ofKakao(userNameAttributeName, attributes);
        }

        // 지원하지 않는 SocialType이 들어온 경우 예외를 발생시킵니다.
        throw new IllegalArgumentException("지원하지 않는 소셜 타입입니다: " + socialType);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    /**
     * of메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2UserInfo가 소셜 타입별로 주입된 상태
     * OAuth2UserInfo에서 socialId(식별값), nickname, imageUrl을 가져와서 build
     * role은 USER로 설정
     */
    public User toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo){
        return User.builder()
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .nickname(oauth2UserInfo.getNickname())
                .imageUrl(oauth2UserInfo.getImageUrl())
                .email(oauth2UserInfo.getEmail())
                .role(Role.USER)
                .build();
    }
}
