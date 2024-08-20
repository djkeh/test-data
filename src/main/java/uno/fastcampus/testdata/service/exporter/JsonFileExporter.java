package uno.fastcampus.testdata.service.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.dto.SchemaFieldDto;
import uno.fastcampus.testdata.dto.TableSchemaDto;
import uno.fastcampus.testdata.service.generator.MockDataGeneratorContext;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class JsonFileExporter implements MockDataFileExporter {

    private final MockDataGeneratorContext mockDataGeneratorContext;
    private final ObjectMapper mapper;

    @Override
    public ExportFileType getType() {
        return ExportFileType.JSON;
    }

    @Override
    public String export(TableSchemaDto dto, Integer rowCount) {
        try {
            List<Map<String, Object>> list = new ArrayList<>();

            IntStream.range(0, rowCount).forEach(i -> {
                Map<String, Object> map = new LinkedHashMap<>();
                dto.schemaFields().stream()
                        .sorted(Comparator.comparing(SchemaFieldDto::fieldOrder))
                        .forEach(field -> {
                            String generatedValue = mockDataGeneratorContext.generate(
                                    field.mockDataType(),
                                    field.blankPercent(),
                                    field.typeOptionJson(),
                                    field.forceValue()
                            );
                            if (generatedValue == null) {
                                map.put(field.fieldName(), null);
                            } else {
                                var jsonValue = switch (field.mockDataType().jsonType()) {
                                    case NUMBER -> Long.valueOf(generatedValue);
                                    case BOOLEAN -> Boolean.valueOf(generatedValue);
                                    default -> generatedValue;
                                };
                                map.put(field.fieldName(), jsonValue);
                            }
                        });
                list.add(map);
            });

            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.warn("테이블 스키마 데이터를 JSON으로 변환하는데 실패했습니다 - {}", dto, e);
            return "";
        }
    }

}
