package uno.fastcampus.testdata.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uno.fastcampus.testdata.domain.TableSchema;
import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.dto.TableSchemaDto;
import uno.fastcampus.testdata.repository.TableSchemaRepository;
import uno.fastcampus.testdata.service.exporter.MockDataFileExporterContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Service] 스키마 파일 출력 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SchemaExportServiceTest {

    @InjectMocks private SchemaExportService sut;

    @Mock private MockDataFileExporterContext mockDataFileExporterContext;
    @Mock private TableSchemaRepository tableSchemaRepository;

    @DisplayName("출력 파일 유형과 스키마 정보와 행 수가 주어지면, 엔티티 출력 여부를 마킹하고 알맞은 파일 유형으로 가짜 테이터 파일을 반환한다.")
    @Test
    void givenFileTypeAndSchemaAndRowCount_whenExporting_thenMarksEntityExportedAndReturnsFileFormattedString() {
        // Given
        ExportFileType exportFileType = ExportFileType.CSV;
        TableSchemaDto dto = TableSchemaDto.of("test_schema", "uno", null, null);
        int rowCount = 5;
        TableSchema exectedTableSchema = TableSchema.of(dto.schemaName(), dto.userId());
        given(tableSchemaRepository.findByUserIdAndSchemaName(dto.userId(), dto.schemaName()))
                .willReturn(Optional.of(exectedTableSchema));
        given(mockDataFileExporterContext.export(exportFileType, dto, rowCount)).willReturn("test,file,format");

        // When
        String result = sut.export(exportFileType, dto, rowCount);

        // Then
        assertThat(result).isEqualTo("test,file,format");
        assertThat(exectedTableSchema.isExported()).isTrue();
        then(tableSchemaRepository).should().findByUserIdAndSchemaName(dto.userId(), dto.schemaName());
        then(mockDataFileExporterContext).should().export(exportFileType, dto, rowCount);
    }

    @DisplayName("입력 파라미터 중에 유저 식별 정보가 없으면, 스키마 테이블 조회를 시도하지 않는다.")
    @Test
    void givenNoUserIdInParams_whenExporting_thenDoesNotTrySelectingTableSchema() {
        // Given
        ExportFileType exportFileType = ExportFileType.CSV;
        TableSchemaDto dto = TableSchemaDto.of("test_schema", null, null, null);
        int rowCount = 5;
        given(mockDataFileExporterContext.export(exportFileType, dto, rowCount)).willReturn("test,file,format");

        // When
        String result = sut.export(exportFileType, dto, rowCount);

        // Then
        assertThat(result).isEqualTo("test,file,format");
        then(tableSchemaRepository).shouldHaveNoInteractions();
        then(mockDataFileExporterContext).should().export(exportFileType, dto, rowCount);
    }

}
