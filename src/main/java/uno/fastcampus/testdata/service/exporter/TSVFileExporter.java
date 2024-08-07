package uno.fastcampus.testdata.service.exporter;

import org.springframework.stereotype.Component;
import uno.fastcampus.testdata.domain.constant.ExportFileType;

@Component
public class TSVFileExporter extends DelimiterBasedFileExporter implements MockDataFileExporter {

    @Override
    public String getDelimiter() {
        return "\t";
    }

    @Override
    public ExportFileType getType() {
        return ExportFileType.TSV;
    }

}
