package uno.fastcampus.testdata.dto.request;

import lombok.*;
import uno.fastcampus.testdata.dto.TableSchemaDto;

import java.util.List;
import java.util.stream.Collectors;

// TODO: 스프링 부트 3.4를 쓸 수 있게 되면, record로 되돌리는 것을 검토하기
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
public class TableSchemaRequest {

    private String schemaName;
    private String userId;
    private List<SchemaFieldRequest> schemaFields;

    public TableSchemaDto toDto() {
        return TableSchemaDto.of(
                schemaName,
                userId,
                null,
                schemaFields.stream()
                        .map(SchemaFieldRequest::toDto)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }

}
