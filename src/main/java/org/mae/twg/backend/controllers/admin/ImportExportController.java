package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.services.ImportExportService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
@RequestMapping("/admin/import-export")
public class ImportExportController {
    private final ImportExportService service;

    @GetMapping("/tours/csv/download")
    @Operation(summary = "Получить CSV файл с турами",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<Resource> toursToCSV() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tours.csv")
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(service.loadToursToCSV());
    }

}
