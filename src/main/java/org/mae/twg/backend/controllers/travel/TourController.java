package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.CommentDTO;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.dto.travel.request.geo.TourGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.TourLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.TourLogicDTO;
import org.mae.twg.backend.dto.travel.response.comments.TourCommentDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.http.HttpStatus;
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

    private void validatePageable(Integer page, Integer size) {
        if (page != null && size == null || page == null && size != null) {
            throw new ValidationException("Only both 'page' and 'size' params required");
        }
    }

    @GetMapping("/find/title")
    @Operation(summary = "Туры по координатам")
    public ResponseEntity<List<TourDTO>> findByTitle(@PathVariable Localization local,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer size,
                                                     @RequestParam(required = false) String title) {
        validatePageable(page, size);
        return ResponseEntity.ok(getService().findByTitle(title, local, page, size));
    }

    @GetMapping("/find/filters")
    @Operation(summary = "Туры по координатам")
    public ResponseEntity<List<TourDTO>> findByFilters(@PathVariable Localization local,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer size,
                                                       @RequestParam(required = false) List<Long> countryIds,
                                                       @RequestParam(required = false) List<Long> tagIds,
                                                       @RequestParam(required = false) List<TourType> types,
                                                       @RequestParam(required = false) Integer minDuration,
                                                       @RequestParam(required = false) Integer maxDuration,
                                                       @RequestParam(required = false) Long minCost,
                                                       @RequestParam(required = false) Long maxCost,
                                                       @RequestParam(required = false) List<Stars> stars,
                                                       @RequestParam(required = false) List<Long> resortIds) {
        validatePageable(page, size);
        return ResponseEntity.ok(getService().findByFilters(
                countryIds, tagIds, types,
                minDuration, maxDuration,
                minCost, maxCost,
                stars, resortIds,
                local, page, size));
    }


    @PostMapping("/{id}/image/upload")
    @Operation(summary = "Добавить обложку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> uploadImage(@PathVariable Localization local,
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

    @GetMapping("/find/geo")
    @Operation(summary = "Туры по координатам")
    public ResponseEntity<List<TourDTO>> findByGeo(@PathVariable Localization local,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam Double minLongitude,
                                                   @RequestParam Double maxLongitude,
                                                   @RequestParam Double minLatitude,
                                                   @RequestParam Double maxLatitude) {
        validatePageable(page, size);
        return ResponseEntity.ok(getService().findByGeoData(
                minLongitude, maxLongitude,
                minLatitude, maxLatitude,
                local, page, size));
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

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение отзывов")
    public ResponseEntity<List<TourCommentDTO>> getComments(@PathVariable Long id,
                                                            @RequestParam(required = false) Integer page,
                                                            @RequestParam(required = false) Integer size) {
        log.info("Получение отзывов к туру с id = " + id);
        if (page == null && size == null) {
            return ResponseEntity.ok(getService().getAllCommentsById(id));
        }
        if (page == null || size == null) {
            throw new ValidationException("Only both 'page' and 'size' params are required");
        }
        return ResponseEntity.ok(getService().getPaginatedCommentsById(id, page, size));
    }

    @PostMapping("/{id}/comments/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Добавить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourCommentDTO> createComment(@PathVariable Long id,
                                                        @RequestBody CommentDTO commentDTO) {
        log.info("Добавление отзыва к туру с id = " + id);
        return new ResponseEntity<>(getService().addComment(id, commentDTO), HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}/comments/my/delete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        log.info("Удаление отзыва");
        getService().deleteComment(id);
        return ResponseEntity.ok("Comment was deleted");
    }

    @PutMapping("/{id}/comments/my/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourCommentDTO> updateComment(@PathVariable Long id,
                                                        @RequestBody CommentDTO commentDTO) {
        log.info("Изменение отзыва");
        return ResponseEntity.ok(getService().updateComment(id, commentDTO));
    }
}
