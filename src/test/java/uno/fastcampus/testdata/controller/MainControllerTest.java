package uno.fastcampus.testdata.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uno.fastcampus.testdata.config.SecurityConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[Controller] 메인 컨트롤러 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)
record MainControllerTest(
        @Autowired MockMvc mvc
) {

    @DisplayName("[GET] 메인(루트) 페이지 -> 메인 뷰 (정상)")
    @Test
    void givenNothing_whenEnteringRootPage_thenShowsMainView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/table-schema"));
    }

}
