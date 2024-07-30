package uno.fastcampus.testdata.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import uno.fastcampus.testdata.domain.MockData;
import uno.fastcampus.testdata.domain.SchemaField;
import uno.fastcampus.testdata.domain.TableSchema;
import uno.fastcampus.testdata.domain.constant.MockDataType;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DisplayName("[Repository] JPA 테스트")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class JpaRepositoryTest {

    private static final String TEST_AUDITOR = "test_uno";

    @Autowired private MockDataRepository mockDataRepository;
    @Autowired private SchemaFieldRepository schemaFieldRepository;
    @Autowired private TableSchemaRepository tableSchemaRepository;

    @Autowired private TestEntityManager entityManager;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void selectTest() {
        // Given

        // When
        List<MockData> mockDataList = mockDataRepository.findAll();
        List<SchemaField> schemaFields = schemaFieldRepository.findAll();
        List<TableSchema> tableSchemas = tableSchemaRepository.findAll();

        // Then
        assertThat(mockDataList).hasSize(100);
        assertThat(schemaFields)
                .hasSize(4)
                .first()
                .extracting("tableSchema")
                .isEqualTo(tableSchemas.getFirst());
        assertThat(tableSchemas)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("schemaName", "test_schema1")
                .hasFieldOrPropertyWithValue("userId", "djkeh")
                .extracting("schemaFields", InstanceOfAssertFactories.COLLECTION)
                .hasSize(4);
    }

    @Test
    void insertTest() {
        // Given
        TableSchema tableSchema = TableSchema.of("test_schema", "uno");
        tableSchema.addSchemaFields(List.of(
                SchemaField.of("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                SchemaField.of("age", MockDataType.NUMBER, 2, 10, null, null),
                SchemaField.of("name", MockDataType.NAME, 3, 20, null, null)
        ));

        // When
        TableSchema saved = tableSchemaRepository.save(tableSchema);

        // Then
        entityManager.clear();
        TableSchema newTableSchema = tableSchemaRepository.findById(saved.getId()).orElseThrow();
        assertThat(newTableSchema)
                .hasFieldOrPropertyWithValue("schemaName", "test_schema")
                .hasFieldOrPropertyWithValue("userId", "uno")
                .hasFieldOrPropertyWithValue("createdBy", TEST_AUDITOR)
                .hasFieldOrPropertyWithValue("modifiedBy", TEST_AUDITOR)
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("modifiedAt")
                .extracting("schemaFields", InstanceOfAssertFactories.COLLECTION)
                .hasSize(3)
                .extracting("fieldOrder", Integer.class)
                .containsExactly(1, 2, 3);
        assertThat(newTableSchema.getCreatedAt()).isEqualTo(newTableSchema.getModifiedAt());
    }

    @Test
    void updateTest() {
        TableSchema tableSchema = tableSchemaRepository.findAll().getFirst();
        tableSchema.setSchemaName("test_modified");
        tableSchema.clearSchemaFields();
        tableSchema.addSchemaField(SchemaField.of("age", MockDataType.NUMBER, 3, 0, json(Map.of("min", 1, "max", 30)), null));

        // When
        TableSchema updated = tableSchemaRepository.saveAndFlush(tableSchema);

        // Then
        assertThat(updated)
                .hasFieldOrPropertyWithValue("schemaName", "test_modified")
                .hasFieldOrPropertyWithValue("createdBy", "uno")
                .hasFieldOrPropertyWithValue("modifiedBy", TEST_AUDITOR)
                .extracting("schemaFields", InstanceOfAssertFactories.COLLECTION)
                .hasSize(1);
        assertThat(updated.getCreatedAt()).isBefore(updated.getModifiedAt());
    }

    @Test
    void deleteTest() {
        // Given
        TableSchema tableSchema = tableSchemaRepository.findAll().getFirst();

        // When
        tableSchemaRepository.delete(tableSchema);

        // Then
        assertThat(tableSchemaRepository.count()).isEqualTo(0);
        assertThat(schemaFieldRepository.count()).isEqualTo(0);
    }

    @Test
    void insertUKConstraintTest() {
        // Given
        MockData mockData1 = MockData.of(MockDataType.CAR, "벤츠");
        MockData mockData2 = MockData.of(MockDataType.CAR, "벤츠");

        // When
        Throwable t = catchThrowable(() -> mockDataRepository.saveAll(List.of(mockData1, mockData2)));

        // Then
        assertThat(t)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasCauseInstanceOf(ConstraintViolationException.class)
                .hasRootCauseInstanceOf(SQLIntegrityConstraintViolationException.class)
                .rootCause()
                .message()
                .contains("Unique index or primary key violation"); // H2 메시지
//                .contains("Duplicate entry 'CAR-벤츠'"); // mysql 메시지
    }


    private String json(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException("JSON 반환 테스트중 오류 발생", jpe);
        }
    }

    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig {
        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of(TEST_AUDITOR);
        }
    }

}
