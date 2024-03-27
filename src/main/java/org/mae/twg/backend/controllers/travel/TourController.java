package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.dto.travel.request.geo.TourGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.TourLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.TourLogicDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/tours")
@Tag(name = "Туры")
@Log4j2
public class TourController extends BaseController<TourService, TourDTO, TourLocalDTO> {
    public TourController(TourService service) {
        super(service);
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourDTO> uploadImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          List<MultipartFile> images) throws IOException {
        log.info("Добавление фотографий к отелю");
        if (images == null) {
            throw new ValidationException("Пустой список фотографий");
        }
        return ResponseEntity.ok(getService().uploadImages(id, local, images));
    }

    @DeleteMapping("/{id}/images/delete")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Удалить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourDTO> deleteImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          @RequestBody List<String> images) {
        log.info("Delete images from hotel with id = " + id);
        if (images == null) {
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(getService().deleteImages(id, local, images));
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать тур по id или slug")
    public ResponseEntity<TourDTO> get(@PathVariable Localization local,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            log.error("One of id or slug is required");
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            log.info("Отдать тур по id");
            return ResponseEntity.ok(getService().getById(id, local));
        }
        log.info("Отдать тур по slug");
        return ResponseEntity.ok(getService().getBySlug(slug, local));
    }

    @PutMapping("/{id}/logic/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновить логическую информацию тура",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourDTO> updateLogicData(@PathVariable Long id,
                                             @PathVariable Localization local,
                                             @Valid @RequestBody TourLogicDTO tourDTO) {
        log.info("Обновить логическую информацию тура с id = " + id);
        return ResponseEntity.ok(getService().updateLogicData(id, tourDTO, local));
    }

    @PutMapping("/{id}/geo/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновить геоданные тура",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourDTO> updateGeoData(@PathVariable Long id,
                                           @PathVariable Localization local,
                                           @Valid @RequestBody TourGeoDTO tourDTO) {
        log.info("Обновить геоданные тура с id = " + id);
        return ResponseEntity.ok(getService().updateGeoData(id, tourDTO, local));
    }
}
