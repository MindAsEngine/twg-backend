package org.mae.twg.backend.controllers.travel;

import jakarta.validation.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.SightDTO;
import org.mae.twg.backend.dto.travel.request.SightRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/sights")
@Tag(name = "Точки интереса")
@Log4j2
public class SightController extends BaseTravelController<SightService, SightRequestDTO, SightRequestDTO> {

    public SightController(SightService service) {
        super(service);
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать точку интереса по id или по slug",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> get(@PathVariable Localization local,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            return ResponseEntity.ok(getService().getById(id, local));
        }
        return ResponseEntity.ok(getService().getBySlug(slug, local));
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> uploadImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          List<MultipartFile> images) throws IOException {
        log.info("Добавление фотографий к отелю");
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
