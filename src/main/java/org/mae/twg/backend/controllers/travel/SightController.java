package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.geo.SightGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.SightLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.SightLogicDTO;
import org.mae.twg.backend.dto.travel.response.SightDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/sights")
@Tag(name = "Точки интереса")
@Log4j2
public class SightController extends BaseController<SightService, SightDTO, SightLocalDTO> {

    public SightController(SightService service) {
        super(service);
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать точку интереса по id или по slug")
    public ResponseEntity<SightDTO> get(@PathVariable Localization local,
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


    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @PostMapping("/{id}/geo/update")
    public ResponseEntity<SightDTO> updateGeoData(@PathVariable Localization local,
                                           @PathVariable Long id,
                                           @RequestBody SightGeoDTO sightDTO) {
        log.info("Изменение геоданных точки интереса с id = " + id);
        return ResponseEntity.ok(getService().updateGeoData(id, sightDTO, local));
    }

    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @PostMapping("/{id}/logic/update")
    public ResponseEntity<SightDTO> updateLogicData(@PathVariable Localization local,
                                           @PathVariable Long id,
                                           @RequestBody SightLogicDTO sightDTO) {
        log.info("Изменение логических данных точки интереса с id = " + id);
        return ResponseEntity.ok(getService().updateLogicData(id, sightDTO, local));
    }

    @PostMapping("/{id}/image/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить обложку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<SightDTO> uploadImage(@PathVariable Localization local,
                                         @PathVariable Long id,
                                         MultipartFile image) throws IOException {
        log.info("Добавление обложки к отелю");
        if (image == null) {
            throw new ValidationException("Нет фотографии");
        }
        return ResponseEntity.ok(getService().uploadImage(id, local, image));
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<SightDTO> uploadImages(@PathVariable Localization local,
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
    public ResponseEntity<SightDTO> deleteImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          @RequestBody List<String> images) {
        log.info("Delete images from hotel with id = " + id);
        if (images == null) {
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(getService().deleteImages(id, local, images));
    }


}
