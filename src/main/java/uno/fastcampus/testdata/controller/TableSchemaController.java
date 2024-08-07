package uno.fastcampus.testdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uno.fastcampus.testdata.domain.constant.ExportFileType;
import uno.fastcampus.testdata.domain.constant.MockDataType;
import uno.fastcampus.testdata.dto.request.TableSchemaExportRequest;
import uno.fastcampus.testdata.dto.request.TableSchemaRequest;
import uno.fastcampus.testdata.dto.response.SchemaFieldResponse;
import uno.fastcampus.testdata.dto.response.SimpleTableSchemaResponse;
import uno.fastcampus.testdata.dto.response.TableSchemaResponse;
import uno.fastcampus.testdata.dto.security.GithubUser;
import uno.fastcampus.testdata.service.SchemaExportService;
import uno.fastcampus.testdata.service.TableSchemaService;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TableSchemaController {

    private final TableSchemaService tableSchemaService;
    private final SchemaExportService schemaExportService;
    private final ObjectMapper mapper;

    @GetMapping("/table-schema")
    public String tableSchema(
            @AuthenticationPrincipal GithubUser githubUser,
            @RequestParam(required = false) String schemaName,
            Model model
    ) {
        TableSchemaResponse tableSchema = (githubUser != null && schemaName != null) ?
                TableSchemaResponse.fromDto(tableSchemaService.loadMySchema(githubUser.id(), schemaName)) :
                defaultTableSchema(schemaName);

        model.addAttribute("tableSchema", tableSchema);
        model.addAttribute("mockDataTypes", MockDataType.toObjects());
        model.addAttribute("fileTypes", Arrays.stream(ExportFileType.values()).toList());

        return "table-schema";
    }

    @PostMapping("/table-schema")
    public String createOrUpdateTableSchema(
            @AuthenticationPrincipal GithubUser githubUser,
            TableSchemaRequest tableSchemaRequest,
            RedirectAttributes redirectAttrs
    ) {
        tableSchemaService.upsertTableSchema(tableSchemaRequest.toDto(githubUser.id()));

        redirectAttrs.addAttribute("schemaName", tableSchemaRequest.getSchemaName());

        return "redirect:/table-schema";
    }

    @GetMapping("/table-schema/my-schemas")
    public String mySchemas(
            @AuthenticationPrincipal GithubUser githubUser,
            Model model
    ) {
        List<SimpleTableSchemaResponse> tableSchemas = tableSchemaService.loadMySchemas(githubUser.id())
                .stream()
                .map(SimpleTableSchemaResponse::fromDto)
                .toList();

        model.addAttribute("tableSchemas", tableSchemas);

        return "my-schemas";
    }

    @PostMapping("/table-schema/my-schemas/{schemaName}")
    public String deleteMySchema(
            @AuthenticationPrincipal GithubUser githubUser,
            @PathVariable String schemaName
    ) {
        tableSchemaService.deleteTableSchema(githubUser.id(), schemaName);

        return "redirect:/table-schema/my-schemas";
    }

    @GetMapping("/table-schema/export")
    public ResponseEntity<String> exportTableSchema(
            @AuthenticationPrincipal GithubUser githubUser,
            TableSchemaExportRequest tableSchemaExportRequest
    ) {
        String body = schemaExportService.export(
                tableSchemaExportRequest.getFileType(),
                tableSchemaExportRequest.toDto(githubUser != null ? githubUser.id() : null),
                tableSchemaExportRequest.getRowCount()
        );
        String filename = tableSchemaExportRequest.getSchemaName() + "." + tableSchemaExportRequest.getFileType().name().toLowerCase();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(body);
    }


    private TableSchemaResponse defaultTableSchema(String schemaName) {
        return new TableSchemaResponse(
                schemaName != null ? schemaName : "schema_name",
                "Uno",
                List.of(
                        new SchemaFieldResponse("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        new SchemaFieldResponse("name", MockDataType.NAME, 2, 10, null, null),
                        new SchemaFieldResponse("age", MockDataType.NUMBER, 3, 20, null, null),
                        new SchemaFieldResponse("my_car", MockDataType.CAR, 4, 50, null, null)
                )
        );
    }

}
