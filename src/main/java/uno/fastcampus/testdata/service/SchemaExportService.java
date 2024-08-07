package uno.fastcampus.testdata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uno.fastcampus.testdata.domain.TableSchema;
import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.dto.TableSchemaDto;
import uno.fastcampus.testdata.repository.TableSchemaRepository;
import uno.fastcampus.testdata.service.exporter.MockDataFileExporterContext;

@RequiredArgsConstructor
@Service
public class SchemaExportService {

    private final MockDataFileExporterContext mockDataFileExporterContext;
    private final TableSchemaRepository tableSchemaRepository;

    public String export(ExportFileType fileType, TableSchemaDto dto, Integer rowCount) {
        if (dto.userId() != null) {
            tableSchemaRepository.findByUserIdAndSchemaName(dto.userId(), dto.schemaName())
                    .ifPresent(TableSchema::markExported);
        }

        return mockDataFileExporterContext.export(fileType, dto, rowCount);
    }

}
