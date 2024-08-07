package uno.fastcampus.testdata.service.exporter;

import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.dto.TableSchemaDto;

/**
 * 특정 파일 유형({@link ExportFileType})에 맞는 데이터 출력 인터페이스.
 */
public interface MockDataFileExporter {

    /**
     * 구현체가 처리하는 출력 파일 유형을 반환하는 메소드.
     *
     * @return 이 구현체가 다루는 출력 파일 유형
     */
    ExportFileType getType();

    /**
     * 파일 형식에 맞는 문자열 데이터를 출력하는 메소드.
     *
     * @param dto 출력 데이터의 테이블 스키마 정보
     * @param rowCount 출력할 데이터 행 수
     * @return 적절한 파일 형식으로 변환된 문자열 데이터
     */
    String export(TableSchemaDto dto, Integer rowCount);

}
