package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.CountryRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/countries")
@Tag(name = "Страны")
@Log4j2
public class CountryController extends BaseTravelController<CountryService, CountryRequestDTO, CountryRequestDTO> {
    public CountryController(CountryService service) {
        super(service);
    }
    @PostMapping("/{id}/images/upload")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> uploadImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          List<MultipartFile> images) throws IOException {
        log.info("Добавление фотографии к стране");
        if (images == null) {
            throw new ValidationException("Пустой список фотографий");
        }
        return ResponseEntity.ok(getService().uploadImages(id, local, images));
    }

    @DeleteMapping("/{id}/images/delete")
    @Operation(summary = "Удалить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> deleteImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          @RequestBody List<String> images) {
        log.info("Delete images from hotel with id = " + id);
        if (images == null) {
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(getService().deleteImages(id, local, images));
    }
}
