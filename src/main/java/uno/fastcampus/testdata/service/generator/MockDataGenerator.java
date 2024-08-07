package uno.fastcampus.testdata.service.generator;

import uno.fastcampus.testdata.domain.constant.MockDataType;

/**
 * 가짜 데이터를 발생시키는 인터페이스
 *
 * @author Uno
 */
public interface MockDataGenerator {

    /**
     * 구현체가 처리하는 가짜 데이터 타입을 반환하는 메소드.
     *
     * @return 이 구현체가 다루는 가짜 데이터의 유형
     */
    MockDataType getType();

    /**
     * 가짜 데이터를 생성하는 메소드.
     *
     * @param blankPercent 빈 값의 비율, 백분율(0 ~ 100). 0이면 빈 값이 없음, 100이면 모두 빈 값({@code null}).
     * @param typeOptionJson 가짜 데이터 생성에 필요한 부가 옵션을 담은 JSON 문자열. 구현체에 따라 다르게 해석됨.
     * @param forceValue 강제로 설정할 값. 이 값이 주어지면, 가짜 데이터를 생성하지 않고 무조건 이 값으로 반환함.
     * @return 생성된 가짜 데이터 문자열
     */
    String generate(Integer blankPercent, String typeOptionJson, String forceValue);

}
