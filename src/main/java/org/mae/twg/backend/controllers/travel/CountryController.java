package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.geo.CountryGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.CountryLocalDTO;
import org.mae.twg.backend.dto.travel.response.CountryDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.services.travel.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/countries")
@Tag(name = "Страны")
@Log4j2
public class CountryController extends BaseController<CountryService, CountryDTO, CountryLocalDTO> {
    public CountryController(CountryService service) {
        super(service);
    }

    private void validatePageable(Integer page, Integer size) {
        if (page != null && size == null || page == null && size != null) {
            log.warn("Only both 'page' and 'size' params required");
            throw new ValidationException("Only both 'page' and 'size' params required");
        }
    }

    @GetMapping("/find/filters")
    @Operation(summary = "Получение стран по фильтрам")
    public ResponseEntity<List<CountryDTO>> getByFilters(@PathVariable Localization local,
                                                         @RequestParam(required = false) List<TourType> tourTypes,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size) {
        validatePageable(page, size);
        if (tourTypes == null) {
            log.warn("tourTypes is empty");
            tourTypes = List.of();
        }
        log.info("Get countries by filters: tour types = " + tourTypes);
        return ResponseEntity.ok(getService().getByFilters(tourTypes, local, page, size));
    }

    @PutMapping("/{id}/geo/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Установка новых геоднанных",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<CountryDTO> updateGeo(@PathVariable Localization local,
                                       @PathVariable Long id,
                                       @RequestBody CountryGeoDTO geoData) {
        log.info("Set geo data for country with id = " + id);
        return ResponseEntity.ok(getService().updateGeo(id, geoData, local));
    }

    @PostMapping("/{id}/images/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> uploadImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          List<MultipartFile> images) throws IOException {
        log.info("Добавление фотографии к стране c id: " + id);
        if (images == null) {
            log.warn("Пустой список фотографий");
            throw new ValidationException("Пустой список фотографий");
        }
        return ResponseEntity.ok(getService().uploadImages(id, local, images));
    }

    @DeleteMapping("/{id}/images/delete")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
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
