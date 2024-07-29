package uno.fastcampus.testdata.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 단위 테이블 스키마 정보.
 * 식별자({@link #userId})로 특정할 수 있는 회원이 소유한다.
 *
 * @author Uno
 */
@Getter
@ToString(callSuper = true)
@Entity
public class TableSchema extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter private String schemaName;
    @Setter private String userId;
    @Setter private LocalDateTime exportedAt;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;

    @ToString.Exclude
    @OneToMany(mappedBy = "tableSchema", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<SchemaField> schemaFields = new LinkedHashSet<>();


    protected TableSchema() {}

    public TableSchema(String schemaName, String userId) {
        this.schemaName = schemaName;
        this.userId = userId;
        this.exportedAt = null;
    }

    public static TableSchema of(String schemaName, String userId) {
        return new TableSchema(schemaName, userId);
    }


    public void markExported() {
        exportedAt = LocalDateTime.now();
    }

    public boolean isExported() {
        return exportedAt != null;
    }

    public void addSchemaField(SchemaField schemaField) {
        schemaField.setTableSchema(this);
        schemaFields.add(schemaField);
    }

    public void addSchemaFields(Collection<SchemaField> schemaFields) {
        schemaFields.forEach(this::addSchemaField);
    }

    public void clearSchemaFields() {
        schemaFields.clear();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableSchema that)) return false;

        if (that.getId() == null) {
            return Objects.equals(this.getSchemaName(), that.getSchemaName()) &&
                    Objects.equals(this.getUserId(), that.getUserId()) &&
                    Objects.equals(this.getExportedAt(), that.getExportedAt()) &&
                    Objects.equals(this.getSchemaFields(), that.getSchemaFields());
        }

        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            return Objects.hash(getSchemaName(), getUserId(), getExportedAt(), getSchemaFields());
        }

        return Objects.hash(getId());
    }

}
