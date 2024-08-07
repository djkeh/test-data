package uno.fastcampus.testdata.service.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uno.fastcampus.testdata.domain.MockData;
import uno.fastcampus.testdata.domain.constant.MockDataType;
import uno.fastcampus.testdata.repository.MockDataRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("[Logic] 문자열 데이터 생성기 테스트")
@ExtendWith(MockitoExtension.class)
class StringGeneratorTest {

    @InjectMocks private StringGenerator sut;

    @Mock private MockDataRepository mockDataRepository;
    @Spy private ObjectMapper mapper;

    @DisplayName("이 테스트 문자열 타입의 가짜 데이터를 다룬다.")
    @Test
    void givenNothing_whenCheckingType_thenReturnsStringType() {
        // Given

        // When & Then
        assertThat(sut.getType()).isEqualTo(MockDataType.STRING);
    }

    @DisplayName("옵션에 따라 정규식을 통과하는 랜덤 문자열을 생성한다.")
    @RepeatedTest(10)
    void givenParams_whenGenerating_thenReturnsRandomText() throws Exception {
        // Given
        MockDataType mockDataType = sut.getType();
        StringGenerator.Option option = new StringGenerator.Option(1, 10);
        String optionJson = mapper.writeValueAsString(option);
        given(mockDataRepository.findByMockDataType(mockDataType)).willReturn(List.of(
                MockData.of(mockDataType, "새침하게 흐린 품이 눈이 올 듯하더니 눈은 아니 오고 얼다가 만 비가 추적추적 내리었다."),
                MockData.of(mockDataType, "이날이야말로 동소문 안에서 인력거꾼 노릇을 하는 김 첨지에게는 오래간만에도 닥친 운수 좋은 날이었다."),
                MockData.of(mockDataType, "문안에(거기도 문밖은 아니지만) 들어간답시는 앞집 마나님을 전찻길까지 모셔다 드린 것을 비롯으로 행여나 손님이 있을까 하고 정류장에서 어정어정하며 내리는 사람 하나하나에게 거의 비는 듯한 눈결을 보내고 있다가 마침내 교원인 듯한 양복장이를 동광학교(東光學校)까지 태워다 주기로 되었다.")
        ));

        // When
        String result = sut.generate(0, optionJson, null);

        // Then
        System.out.println(result); // 관찰용
        assertThat(result).containsPattern("^[가-힣]{1,10}$");
        then(mapper).should().readValue(optionJson, StringGenerator.Option.class);
        then(mockDataRepository).should().findByMockDataType(mockDataType);
    }

    @CsvSource(delimiter = '|', textBlock = """
            {"minLength":1,"maxLength":1}   |   ^[가-힣]{1,1}$
            {"minLength":1,"maxLength":2}   |   ^[가-힣]{1,2}$
            {"minLength":1,"maxLength":3}   |   ^[가-힣]{1,3}$
            {"minLength":1,"maxLength":10}  |   ^[가-힣]{1,10}$
            {"minLength":5,"maxLength":10}  |   ^[가-힣]{5,10}$
            {"minLength":6,"maxLength":10}  |   ^[가-힣]{6,10}$
            {"minLength":7,"maxLength":10}  |   ^[가-힣]{7,10}$
            {"minLength":9,"maxLength":10}  |   ^[가-힣]{9,10}$
    """)
    @DisplayName("옵션에 따라 정규식을 통과하는 랜덤 문자열을 생성한다.")
    @ParameterizedTest(name = "{index}. {0} ===> 통과 정규식: {1}")
    void givenParams_whenGenerating_thenReturnsRandomText(String optionJson, String expectedRegex) throws Exception {
        // Given
        MockDataType mockDataType = sut.getType();
        given(mockDataRepository.findByMockDataType(mockDataType)).willReturn(List.of(
                MockData.of(mockDataType, "새침하게 흐린 품이 눈이 올 듯하더니 눈은 아니 오고 얼다가 만 비가 추적추적 내리었다."),
                MockData.of(mockDataType, "이날이야말로 동소문 안에서 인력거꾼 노릇을 하는 김 첨지에게는 오래간만에도 닥친 운수 좋은 날이었다."),
                MockData.of(mockDataType, "문안에(거기도 문밖은 아니지만) 들어간답시는 앞집 마나님을 전찻길까지 모셔다 드린 것을 비롯으로 행여나 손님이 있을까 하고 정류장에서 어정어정하며 내리는 사람 하나하나에게 거의 비는 듯한 눈결을 보내고 있다가 마침내 교원인 듯한 양복장이를 동광학교(東光學校)까지 태워다 주기로 되었다.")
        ));

        // When
        String result = sut.generate(0, optionJson, null);

        // Then
        System.out.println(result); // 관찰용
        assertThat(result).containsPattern("^[가-힣]{1,10}$");
        then(mapper).should().readValue(optionJson, StringGenerator.Option.class);
        then(mockDataRepository).should().findByMockDataType(mockDataType);
    }

    @DisplayName("잘못된 옵션 내용이 주어지면, 예외를 던진다.")
    @CsvSource(delimiter = '|', textBlock = """
            {"minLength":0,"maxLength":0}
            {"minLength":0,"maxLength":1}
            {"minLength":0,"maxLength":10}
            {"minLength":0,"maxLength":11}
            {"minLength":1,"maxLength":11}
            {"minLength":2,"maxLength":1}
            {"minLength":5,"maxLength":1}
            {"minLength":10,"maxLength":9}
    """)
    @ParameterizedTest(name = "{index}. 잘못된 옵션 - {0}")
    void givenWrongOption_whenGenerating_thenThrowsException(String optionJson) throws Exception {
        // Given

        // When
        Throwable t = catchThrowable(() -> sut.generate(0, optionJson, null));

        // Then
        assertThat(t)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[가짜 데이터 생성 옵션 오류]");
        then(mapper).should().readValue(optionJson, StringGenerator.Option.class);
        then(mockDataRepository).shouldHaveNoInteractions();
    }

    @DisplayName("옵션이 없으면, 기본 옵션으로 정규식을 통과하는 랜덤 문자열을 생성한다.")
    @RepeatedTest(10)
    void givenNoParams_whenGenerating_thenReturnsRandomText() {
        // Given
        MockDataType mockDataType = sut.getType();
        given(mockDataRepository.findByMockDataType(mockDataType)).willReturn(List.of(
                MockData.of(mockDataType, "새침하게 흐린 품이 눈이 올 듯하더니 눈은 아니 오고 얼다가 만 비가 추적추적 내리었다."),
                MockData.of(mockDataType, "이날이야말로 동소문 안에서 인력거꾼 노릇을 하는 김 첨지에게는 오래간만에도 닥친 운수 좋은 날이었다."),
                MockData.of(mockDataType, "문안에(거기도 문밖은 아니지만) 들어간답시는 앞집 마나님을 전찻길까지 모셔다 드린 것을 비롯으로 행여나 손님이 있을까 하고 정류장에서 어정어정하며 내리는 사람 하나하나에게 거의 비는 듯한 눈결을 보내고 있다가 마침내 교원인 듯한 양복장이를 동광학교(東光學校)까지 태워다 주기로 되었다.")
        ));

        // When
        String result = sut.generate(0, null, null);

        // Then
        System.out.println(result); // 관찰용
        assertThat(result).containsPattern("^[가-힣]{1,10}$");
        then(mapper).shouldHaveNoInteractions();
        then(mockDataRepository).should().findByMockDataType(mockDataType);
    }

    @DisplayName("옵션 json 형식이 잘못되었으면, 기본 옵션으로 정규식을 통과하는 랜덤 문자열을 생성한다.")
    @Test
    void givenWrongOptionJson_whenGenerating_thenReturnsRandomText() throws Exception {
        // Given
        MockDataType mockDataType = sut.getType();
        String wrongJson = "{wrong json format}";
        given(mockDataRepository.findByMockDataType(mockDataType)).willReturn(List.of(
                MockData.of(mockDataType, "새침하게 흐린 품이 눈이 올 듯하더니 눈은 아니 오고 얼다가 만 비가 추적추적 내리었다."),
                MockData.of(mockDataType, "이날이야말로 동소문 안에서 인력거꾼 노릇을 하는 김 첨지에게는 오래간만에도 닥친 운수 좋은 날이었다."),
                MockData.of(mockDataType, "문안에(거기도 문밖은 아니지만) 들어간답시는 앞집 마나님을 전찻길까지 모셔다 드린 것을 비롯으로 행여나 손님이 있을까 하고 정류장에서 어정어정하며 내리는 사람 하나하나에게 거의 비는 듯한 눈결을 보내고 있다가 마침내 교원인 듯한 양복장이를 동광학교(東光學校)까지 태워다 주기로 되었다.")
        ));

        // When
        String result = sut.generate(0, wrongJson, null);

        // Then
        System.out.println(result); // 관찰용
        assertThat(result).containsPattern("^[가-힣]{1,10}$");
        then(mapper).should().readValue(wrongJson, StringGenerator.Option.class);
        then(mockDataRepository).should().findByMockDataType(mockDataType);
    }

    @DisplayName("blank 옵션이 100%면, null을 생성한다.")
    @RepeatedTest(10)
    void givenNoParams_whenGenerating_thenReturnsNull() {
        // Given

        // When
        String result = sut.generate(100, null, null);

        // Then
        assertThat(result).isEqualTo(null);
        then(mapper).shouldHaveNoInteractions();
        then(mockDataRepository).shouldHaveNoInteractions();
    }

    @DisplayName("강제 값이 주어지면, 강제 값을 그대로 반환한다.")
    @Test
    void givenForcedValue_whenGenerating_thenReturnsForcedValue() {
        // Given
        String forceValue = "직접입력";

        // When
        String result = sut.generate(0, null, forceValue);

        // Then
        assertThat(result).isEqualTo(forceValue);
        then(mapper).shouldHaveNoInteractions();
        then(mockDataRepository).shouldHaveNoInteractions();
    }

    @DisplayName("강제 값이 주어지더라도, blank 옵션에 따라 null을 반환할 수 있다.")
    @Test
    void givenBlankOptionAndForcedValue_whenGenerating_thenReturnsNull() {
        // Given
        int blankPercent = 100;
        String forceValue = "직접입력";

        // When
        String result = sut.generate(blankPercent, null, forceValue);

        // Then
        assertThat(result).isNull();
        then(mapper).shouldHaveNoInteractions();
        then(mockDataRepository).shouldHaveNoInteractions();
    }

}
