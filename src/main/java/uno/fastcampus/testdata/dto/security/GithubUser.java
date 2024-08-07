package uno.fastcampus.testdata.dto.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 깃헙 인증 정보를 {@link OAuth2User} 인터페이스에 맞춰 구현한 데이터 클래스
 *
 * @param id 깃헙 로그인 ID
 * @param name 깃헙 사용자 이름
 * @param email 깃헙 사용자 이메일
 * @author Uno
 */
public record GithubUser(
        String id,
        String name,
        String email
) implements OAuth2User {

    /**
     * 깃헙 인증 정보를 우리 서비스 인증 정보로 변환하는 메소드
     *
     * @param attributes 인증 정보({@link OAuth2User}) 안에서 꺼낸 깃헙 속성 정보
     * @see "https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#get-the-authenticated-user"
     */
    public static GithubUser from(Map<String, Object> attributes) {
        return new GithubUser(
                String.valueOf(attributes.get("login")),
                String.valueOf(attributes.get("name")), // nullable
                String.valueOf(attributes.get("email")) // nullable
        );
    }

    @Override public Map<String, Object> getAttributes() { return Map.of(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    @Override public String getName() { return name.equals("null") ? id : name; }

}
