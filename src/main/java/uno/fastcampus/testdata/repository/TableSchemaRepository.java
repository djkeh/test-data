package uno.fastcampus.testdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.fastcampus.testdata.domain.TableSchema;

public interface TableSchemaRepository extends JpaRepository<TableSchema, Long> {
}
