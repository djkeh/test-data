package uno.fastcampus.testdata.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SchemaField {

    private String fieldName;
    private String mockDataType;
    private Integer fieldOrder;
    private Integer blackPercent;
    private String typeOptionJson;
    private String forceValue;

}
