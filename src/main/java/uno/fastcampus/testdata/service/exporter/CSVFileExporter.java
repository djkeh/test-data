package uno.fastcampus.testdata.service.exporter;

import org.springframework.stereotype.Component;
import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.service.generator.MockDataGeneratorContext;


@Component
public class CSVFileExporter extends DelimiterBasedFileExporter implements MockDataFileExporter {

    public CSVFileExporter(MockDataGeneratorContext mockDataGeneratorContext) {
        super(mockDataGeneratorContext);
    }

    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public ExportFileType getType() {
        return ExportFileType.CSV;
    }

}
