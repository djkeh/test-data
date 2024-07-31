package uno.fastcampus.testdata.dto;

import uno.fastcampus.testdata.domain.MockData;
import uno.fastcampus.testdata.domain.constant.MockDataType;

public record MockDataDto(
        Long id,
        MockDataType mockDataType,
        String mockDataValue
) {

    public static MockDataDto fromEntity(MockData entity) {
        return new MockDataDto(
                entity.getId(),
                entity.getMockDataType(),
                entity.getMockDataValue()
        );
    }

}
