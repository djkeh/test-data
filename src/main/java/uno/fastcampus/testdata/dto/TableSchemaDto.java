package uno.fastcampus.testdata.dto;

import uno.fastcampus.testdata.domain.TableSchema;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record TableSchemaDto(
        Long id,
        String schemaName,
        String userId,
        LocalDateTime exportedAt,
        Set<SchemaFieldDto> schemaFields,

        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static TableSchemaDto of(Long id, String schemaName, String userId, LocalDateTime exportedAt, Set<SchemaFieldDto> schemaFields, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new TableSchemaDto(id, schemaName, userId, exportedAt, schemaFields, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static TableSchemaDto of(String schemaName, String userId, LocalDateTime exportedAt, Set<SchemaFieldDto> schemaFields) {
        return new TableSchemaDto(null, schemaName, userId, exportedAt, schemaFields, null, null, null, null);
    }

    public static TableSchemaDto fromEntity(TableSchema entity) {
        return new TableSchemaDto(
                entity.getId(),
                entity.getSchemaName(),
                entity.getUserId(),
                entity.getExportedAt(),
                entity.getSchemaFields().stream()
                        .map(SchemaFieldDto::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public TableSchema createEntity() {
        TableSchema entity = TableSchema.of(schemaName, userId);
        entity.addSchemaFields(schemaFields.stream().map(SchemaFieldDto::createEntity).toList());

        return entity;
    }

}
