package uno.fastcampus.testdata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Util][test] Form 데이터 인코더")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {FormDataEncoder.class, ObjectMapper.class}
)
record FormDataEncoderTest(@Autowired FormDataEncoder formDataEncoder) {

    @DisplayName("객체가 주어지면, url 인코딩된 form body data 형식의 문자열을 반환한다.")
    @Test
    void givenObject_whenEncoding_thenReturnsFormEncodedString() {
        // Given
        TestObject obj = new TestObject(
                "This 'is' \"test\" string.",
                List.of("hello", "my", "friend").toString().replace(" ", ""),
                String.join(",", "hello", "my", "friend"),
                null,
                1234,
                3.14,
                false,
                BigDecimal.TEN,
                TestEnum.THREE,
                List.of("one", "two", "three"),
                new TestObject2("hello", 10, true),
                List.of(new TestObject2("ONE", 1, true), new TestObject2("TWO", 2, false))
        );

        // When
        String result = formDataEncoder.encode(obj);

        // Then
        assertThat(result).isEqualTo(
                (
                        "str=This 'is' \"test\" string." +
                                "&listStr1=[hello,my,friend]" +
                                "&listStr2=hello,my,friend" +
//                        "&nullStr=" + // null 파라미터는 제외
                                "&number=1234" +
                                "&floatingNumber=3.14" +
                                "&bool=false" +
                                "&bigDecimal=10" +
                                "&testEnum=THREE" +
                                "&list[0]=one" +
                                "&list[1]=two" +
                                "&list[2]=three" +
                                "&obj.str=hello" +
                                "&obj.number=10" +
                                "&obj.bool=true" +
                                "&objectList[0].str=ONE" +
                                "&objectList[0].number=1" +
                                "&objectList[0].bool=true" +
                                "&objectList[1].str=TWO" +
                                "&objectList[1].number=2" +
                                "&objectList[1].bool=false"
                ).replace("[", "%5B").replace("]", "%5D").replace(" ", "%20").replace("\"", "%22")
        );
    }

    record TestObject(
            String str,
            String listStr1,
            String listStr2,
            String nullStr,
            Integer number,
            Double floatingNumber,
            Boolean bool,
            BigDecimal bigDecimal,
            TestEnum testEnum,
            List<String> list,
            TestObject2 obj,
            List<TestObject2> objectList
    ) {}

    record TestObject2(
            String str,
            Integer number,
            Boolean bool
    ) {}

    enum TestEnum {
        ONE, TWO, THREE
    }

}
