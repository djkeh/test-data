package uno.fastcampus.testdata.domain.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] 테스트 데이터 자료형 테스트")
class MockDataTypeTest {

    @DisplayName("자료형이 주어지면, 해당 원소의 이름을 리턴한다.")
    @Test
    void givenMockDataType_whenReading_thenReturnsEnumElementName() {
        // given
        MockDataType mockDataType = MockDataType.STRING;

        // when
        String elementName = mockDataType.toString();

        // then
        assertThat(elementName).isEqualTo(MockDataType.STRING.name());
    }

    @DisplayName("자료형이 주어지면, 해당 원소의 데이터를 리턴한다.")
    @Test
    void givenMockDataType_whenReading_thenReturnsEnumElementObject() {
        // Given
        MockDataType mockDataType = MockDataType.STRING;

        // When
        MockDataType.MockDataTypeObject result = mockDataType.toObject();

        // Then
        assertThat(result.toString()).contains("name", "requiredOptions", "baseType");
    }

    @CsvSource(textBlock = """
            STRING,     STRING
            NUMBER,     NUMBER
            BOOLEAN,    BOOLEAN
            DATETIME,   DATETIME
            ENUM,       ENUM
            
            SENTENCE,   STRING
            PARAGRAPH,  STRING
            UUID,       STRING
            EMAIL,      STRING
            CAR,        STRING
            ROW_NUMBER, NUMBER
            NAME,       STRING
            """
    )
    @DisplayName("자료형이 주어지면, JSON에 호환되는 자료형을 반환한다.")
    @ParameterizedTest(name = "{index}. {0} => Json Type \"{1}\"")
    void givenMockDataType_whenReading_thenReturnsJsonCompatibleMockDataType(
            MockDataType input,
            MockDataType expected
    ) {
        // Given

        // When
        MockDataType actual = input.jsonType();

        // Then
        assertThat(actual).isEqualTo(expected);
    }

}
