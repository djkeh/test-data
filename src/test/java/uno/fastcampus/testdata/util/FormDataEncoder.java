package uno.fastcampus.testdata.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 테스트 편의를 위해 만든 테스트 전용 유틸리티 클래스.
 * 객체 데이터를 post form data로 인코딩하는 기능을 제공한다.
 */
@TestComponent
public class FormDataEncoder {

    private final ObjectMapper mapper;

    public FormDataEncoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    /**
     * 데이터를 post form data 형식으로 인코딩한다.
     *
     * @param obj 입력 데이터
     * @return form data 형식으로 인코딩된 문자열
     */
    public String encode(Object obj) {
        Map<String, Object> fieldMap = mapper.convertValue(obj, new TypeReference<>() {});
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        fieldMap.forEach((key, value) -> addToBuilder(builder, key, value));

        return builder.build().encode().getQuery();
    }

    private void addToBuilder(UriComponentsBuilder builder, String key, Object value) {
        switch (value) {
            case Map<?, ?> map -> map.forEach((subKey, subValue) -> addToBuilder(builder, key + "." + subKey, subValue));
            case List<?> list -> IntStream.range(0, list.size()).forEach(i -> addToBuilder(builder, key + "[" + i + "]", list.get(i)));
            case null -> {}
            default -> builder.queryParam(key, value.toString());
        }
    }

}
