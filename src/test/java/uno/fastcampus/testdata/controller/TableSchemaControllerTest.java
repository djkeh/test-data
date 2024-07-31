package uno.fastcampus.testdata.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uno.fastcampus.testdata.config.SecurityConfig;
import uno.fastcampus.testdata.domain.constant.MockDataType;
import uno.fastcampus.testdata.dto.request.SchemaFieldRequest;
import uno.fastcampus.testdata.dto.request.TableSchemaRequest;
import uno.fastcampus.testdata.util.FormDataEncoder;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Controller] 테이블 스키마 컨트롤러 테스트")
@Import({SecurityConfig.class, FormDataEncoder.class})
@WebMvcTest
record TableSchemaControllerTest(
        @Autowired MockMvc mvc,
        @Autowired FormDataEncoder formDataEncoder
) {

    @DisplayName("[GET] 테이블 스키마 페이지 -> 테이블 스키마 뷰 (정상)")
    @Test
    void givenNothing_whenRequesting_thenShowsTableSchemaView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("table-schema"));
    }

    @DisplayName("[POST] 테이블 스키마 생성, 변경 (정상)")
    @Test
    void givenTableSchemaRequest_whenCreatingOrUpdating_thenRedirectsToTableSchemaView() throws Exception {
        // Given
        TableSchemaRequest request = TableSchemaRequest.of(
                "test_schema",
                "홍길동",
                List.of(
                        SchemaFieldRequest.of("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        SchemaFieldRequest.of("name", MockDataType.NAME, 2, 10, null, null),
                        SchemaFieldRequest.of("age", MockDataType.NUMBER, 3, 20, null, null)
                )
        );

        // When & Then
        mvc.perform(
                post("/table-schema")
                        .content(formDataEncoder.encode(request))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("tableSchemaRequest", request))
                .andExpect(redirectedUrl("/table-schema"));
    }

    @DisplayName("[GET] 내 스키마 목록 페이지 -> 내 스키마 목록 뷰 (정상)")
    @Test
    void givenAuthenticatedUser_whenRequesting_thenShowsMySchemaView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema/my-schemas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("my-schemas"));
    }

    @DisplayName("[POST] 내 스키마 삭제 (정상)")
    @Test
    void givenAuthenticatedUserAndSchemaName_whenDeleting_thenRedirectsToTableSchemaView() throws Exception {
        // Given
        String schemaName = "test_schema";

        // When & Then
        mvc.perform(
                post("/table-schema/my-schemas/{schemaName}", schemaName)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-schemas"));
    }

    @DisplayName("[GET] 테이블 스키마 파일 다운로드 -> 테이블 스키마 파일 (정상)")
    @Test
    void givenTableSchema_whenDownloading_thenReturnsFile() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema/export"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=table-schema.txt"))
                .andExpect(content().string("download complete!")); // TODO: 나중에 데이터 바꿔야 함
    }

}
