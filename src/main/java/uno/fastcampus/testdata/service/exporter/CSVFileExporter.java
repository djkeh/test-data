package uno.fastcampus.testdata.service.exporter;

import org.springframework.stereotype.Component;
import uno.fastcampus.testdata.domain.constant.ExportFileType;

@Component
public class CSVFileExporter extends DelimiterBasedFileExporter implements MockDataFileExporter {

    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public ExportFileType getType() {
        return ExportFileType.CSV;
    }

}
