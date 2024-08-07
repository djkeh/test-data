package uno.fastcampus.testdata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uno.fastcampus.testdata.config.SecurityConfig;
import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.domain.constant.MockDataType;
import uno.fastcampus.testdata.dto.TableSchemaDto;
import uno.fastcampus.testdata.dto.request.SchemaFieldRequest;
import uno.fastcampus.testdata.dto.request.TableSchemaExportRequest;
import uno.fastcampus.testdata.dto.request.TableSchemaRequest;
import uno.fastcampus.testdata.dto.security.GithubUser;
import uno.fastcampus.testdata.service.TableSchemaService;
import uno.fastcampus.testdata.util.FormDataEncoder;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Controller] 테이블 스키마 컨트롤러 테스트")
@Import({SecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(TableSchemaController.class)
class TableSchemaControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private FormDataEncoder formDataEncoder;
    @Autowired private ObjectMapper mapper;

    @MockBean private TableSchemaService tableSchemaService;

    @DisplayName("[GET] 테이블 스키마 조회, 비로그인 최초 진입 (정상)")
    @Test
    void givenNothing_whenRequesting_thenShowsTableSchemaView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("tableSchema"))
                .andExpect(model().attributeExists("mockDataTypes"))
                .andExpect(model().attributeExists("fileTypes"))
                .andExpect(view().name("table-schema"));
        then(tableSchemaService).shouldHaveNoInteractions();
    }

    @DisplayName("[GET] 테이블 스키마 조회, 로그인 + 특정 테이블 스키마 (정상)")
    @Test
    void givenAuthenticatedUserAndSchemaName_whenRequesting_thenShowsTableSchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        var schemaName = "test_schema";
        given(tableSchemaService.loadMySchema(githubUser.id(), schemaName)).willReturn(TableSchemaDto.of(schemaName, githubUser.id(), null, Set.of()));

        // When & Then
        mvc.perform(
                get("/table-schema")
                        .queryParam("schemaName", schemaName)
                        .with(oauth2Login().oauth2User(githubUser))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("tableSchema"))
//                .andExpect(model().attribute("tableSchema", hasProperty("schemaName", is(schemaName)))) // dto가 record여서 불가능한 방식
                .andExpect(model().attributeExists("mockDataTypes"))
                .andExpect(model().attributeExists("fileTypes"))
                .andExpect(content().string(containsString(schemaName))) // html 전체 검사하므로 정확하지 않은 테스트 방식
                .andExpect(view().name("table-schema"));
        then(tableSchemaService).should().loadMySchema(githubUser.id(), schemaName);
    }

    @DisplayName("[POST] 테이블 스키마 생성, 변경 (정상)")
    @Test
    void givenTableSchemaRequest_whenCreatingOrUpdating_thenRedirectsToTableSchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        TableSchemaRequest request = TableSchemaRequest.of(
                "test_schema",
                List.of(
                        SchemaFieldRequest.of("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        SchemaFieldRequest.of("name", MockDataType.NAME, 2, 10, null, null),
                        SchemaFieldRequest.of("age", MockDataType.NUMBER, 3, 20, null, null)
                )
        );
        willDoNothing().given(tableSchemaService).upsertTableSchema(request.toDto(githubUser.id()));

        // When & Then
        mvc.perform(
                post("/table-schema")
                        .content(formDataEncoder.encode(request))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .with(oauth2Login().oauth2User(githubUser))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/table-schema?schemaName={schemaName}", request.getSchemaName()));
        then(tableSchemaService).should().upsertTableSchema(request.toDto(githubUser.id()));
    }

    @DisplayName("[GET] 내 스키마 목록 조회 (비로그인)")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLogin() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema/my-schemas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/github"));
        then(tableSchemaService).shouldHaveNoInteractions();
    }

    @DisplayName("[GET] 내 스키마 목록 조회 (정상)")
    @Test
    void givenAuthenticatedUser_whenRequesting_thenShowsMySchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        given(tableSchemaService.loadMySchemas(githubUser.id())).willReturn(List.of());

        // When & Then
        mvc.perform(
                get("/table-schema/my-schemas")
                        .with(oauth2Login().oauth2User(githubUser))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attribute("tableSchemas", List.of()))
                .andExpect(view().name("my-schemas"));
        then(tableSchemaService).should().loadMySchemas(githubUser.id());
    }

    @DisplayName("[POST] 내 스키마 삭제 (정상)")
    @Test
    void givenAuthenticatedUserAndSchemaName_whenDeleting_thenRedirectsToTableSchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        String schemaName = "test_schema";
        willDoNothing().given(tableSchemaService).deleteTableSchema(githubUser.id(), schemaName);

        // When & Then
        mvc.perform(
                post("/table-schema/my-schemas/{schemaName}", schemaName)
                        .with(csrf())
                        .with(oauth2Login().oauth2User(githubUser))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/table-schema/my-schemas"));
        then(tableSchemaService).should().deleteTableSchema(githubUser.id(), schemaName);
    }

    @DisplayName("[GET] 테이블 스키마 파일 다운로드 (정상)")
    @Test
    void givenTableSchema_whenDownloading_thenReturnsFile() throws Exception {
        // Given
        TableSchemaExportRequest request = TableSchemaExportRequest.of(
                "test",
                77,
                ExportFileType.CSV,
                List.of(
                        SchemaFieldRequest.of("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        SchemaFieldRequest.of("name", MockDataType.STRING, 1, 0, "option", "well"),
                        SchemaFieldRequest.of("age", MockDataType.NUMBER, 3, 20, null, null)
                )
        );
        String queryParam = formDataEncoder.encode(request, false);

        // When & Then
        mvc.perform(get("/table-schema/export?" + queryParam))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.csv"))
                .andExpect(content().json(mapper.writeValueAsString(request))); // TODO: 나중에 데이터 바꿔야 함
    }

}
