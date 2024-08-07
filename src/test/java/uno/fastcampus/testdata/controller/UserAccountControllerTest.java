package uno.fastcampus.testdata.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uno.fastcampus.testdata.config.SecurityConfig;
import uno.fastcampus.testdata.dto.security.GithubUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Controller] 회원 컨트롤러 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(UserAccountController.class)
record UserAccountControllerTest(@Autowired MockMvc mvc) {

    @DisplayName("[GET] 내 정보 페이지 -> 내 정보 뷰 (정상)")
    @Test
    void givenAuthenticatedUser_whenRequesting_thenShowsMyAccountView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");

        // When & Then
        mvc.perform(
                get("/my-account")
                        .with(oauth2Login().oauth2User(githubUser))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attribute("nickname", githubUser.name()))
                .andExpect(model().attribute("email", githubUser.email()))
                .andExpect(view().name("my-account"));
    }

}
