package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.services.ImportExportService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
//@PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
@RequestMapping("/admin/import-export")
@Tag(name = "Импорт/экспорт")
@Log4j2
public class ImportExportController {
    private final ImportExportService service;

    @GetMapping("/tours/excel/download")
    @Operation(summary = "Получить Excel файл с турами",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<FileSystemResource> toursToExcel() throws IOException {
        log.info("Получить Excel файл с турами");
        FileSystemResource fileResource = new FileSystemResource(service.loadToursToExcel());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tours.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileResource);
    }

    @GetMapping("/tours/excel/upload")
    @Operation(summary = "Загрузить Excel файл с турами",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> excelToTours(@RequestParam MultipartFile file) throws IOException {
        log.info("Отдать excel по турам");
        service.loadToursFromExcel(file);
        return ResponseEntity.ok("Tours was successfully uploaded");
    }

}