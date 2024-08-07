package uno.fastcampus.testdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uno.fastcampus.testdata.domain.MockData;
import uno.fastcampus.testdata.domain.constant.MockDataType;

import java.util.List;

public interface MockDataRepository extends JpaRepository<MockData, Long> {
    List<MockData> findByMockDataType(MockDataType mockDataType);
}
