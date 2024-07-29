package uno.fastcampus.testdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.fastcampus.testdata.domain.MockData;

public interface MockDataRepository extends JpaRepository<MockData, Long> {
}
