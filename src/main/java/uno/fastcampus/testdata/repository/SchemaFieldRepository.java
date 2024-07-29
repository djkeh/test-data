package uno.fastcampus.testdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.fastcampus.testdata.domain.SchemaField;

public interface SchemaFieldRepository extends JpaRepository<SchemaField, Long> {
}
