package uno.fastcampus.testdata.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uno.fastcampus.testdata.domain.TableSchema;

import java.util.Optional;

public interface TableSchemaRepository extends JpaRepository<TableSchema, Long> {
    Page<TableSchema> findByUserId(String userId, Pageable pageable);
    Optional<TableSchema> findByUserIdAndSchemaName(String userId, String schemaName);
    void deleteByUserIdAndSchemaName(String userId, String schemaName);
}
