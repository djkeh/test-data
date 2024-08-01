package uno.fastcampus.testdata.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import uno.fastcampus.testdata.domain.TableSchema;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Repository] 테이블 스키마 쿼리 테스트")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class TableSchemaRepositoryTest {

    @Autowired private TableSchemaRepository sut;

    @DisplayName("사용자 식별자와 페이징 정보가 주어지면, 페이징된 테이블 스키마 목록을 반환한다.")
    @Test
    void givenUserIdAndPagingInfo_whenSelecting_thenReturnsPagedTableSchemas() {
        // Given
        var userId = "djkeh";
        var pageable = Pageable.ofSize(5);

        // When
        Page<TableSchema> result = sut.findByUserId(userId, pageable);

        // Then
        assertThat(result.getContent())
                .hasSize(1)
                .extracting("userId", String.class)
                .containsOnly(userId);
        assertThat(result.getPageable())
                .hasFieldOrPropertyWithValue("pageSize", 5)
                .hasFieldOrPropertyWithValue("pageNumber", 0);
    }

    @DisplayName("사용자 식별자와 테이블 스키마 이름이 주어지면, 테이블 스키마를 반환한다.")
    @Test
    void givenUserIdAndSchemaName_whenSelecting_thenReturnsTableSchema() {
        // Given
        var userId = "djkeh";
        var schemaName = "test_schema1";

        // When
        Optional<TableSchema> result = sut.findByUserIdAndSchemaName(userId, schemaName);

        // Then
        assertThat(result)
                .get()
                .hasFieldOrPropertyWithValue("userId", userId)
                .hasFieldOrPropertyWithValue("schemaName", schemaName);
    }

    @DisplayName("사용자 식별자와 테이블 스키마 이름이 주어지면, 테이블 스키마를 삭제한다.")
    @Test
    void givenUserIdAndSchemaName_whenDeleting_thenDeletesTableSchema() {
        // Given
        var userId = "djkeh";
        var schemaName = "test_schema1";

        // When
        sut.deleteByUserIdAndSchemaName(userId, schemaName);

        // Then
        assertThat(sut.findByUserIdAndSchemaName(userId, schemaName)).isEmpty();
    }

}
