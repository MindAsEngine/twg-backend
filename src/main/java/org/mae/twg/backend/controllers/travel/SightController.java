package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.CommentDTO;
import org.mae.twg.backend.dto.travel.request.geo.SightGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.SightLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.SightLogicDTO;
import org.mae.twg.backend.dto.travel.response.SightDTO;
import org.mae.twg.backend.dto.travel.response.comments.SightCommentDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.springframework.http.HttpStatus;
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

    private void validatePageable(Integer page, Integer size) {
        if (page != null && size == null || page == null && size != null) {
            log.warn("Only both 'page' and 'size' params required");
            throw new ValidationException("Only both 'page' and 'size' params required");
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать точку интереса по id или по slug")
    public ResponseEntity<SightDTO> get(@PathVariable Localization local,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            log.warn("One of id or slug is required");
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            log.info("Отдать точку интереса по id: " + id);
            return ResponseEntity.ok(getService().getById(id, local));
        }
        log.info("Отдать точку интереса по slug: " + slug);
        return ResponseEntity.ok(getService().getBySlug(slug, local));
    }


    @GetMapping("/find/geo")
    @Operation(summary = "Точки интереса по координатам")
    public ResponseEntity<List<SightDTO>> findByGeo(@PathVariable Localization local,
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

    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @PutMapping("/{id}/geo/update")
    public ResponseEntity<SightDTO> updateGeoData(@PathVariable Localization local,
                                           @PathVariable Long id,
                                           @RequestBody SightGeoDTO sightDTO) {
        log.info("Изменение геоданных точки интереса с id = " + id);
        return ResponseEntity.ok(getService().updateGeoData(id, sightDTO, local));
    }

    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @PutMapping("/{id}/logic/update")
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
            log.warn("Нет фотографии");
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
        log.info("Добавление фотографий к отелю c id: " + id);
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
    public ResponseEntity<SightDTO> deleteImages(@PathVariable Localization local,
                                          @PathVariable Long id,
                                          @RequestBody List<String> images) {
        log.info("Delete images from hotel with id = " + id);
        if (images == null) {
            log.warn("Empty images list");
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(getService().deleteImages(id, local, images));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение отзывов")
    public ResponseEntity<List<SightCommentDTO>> getComments(@PathVariable Long id,
                                                             @RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer size) {
        log.info("Получение отзывов к точке интереса с id = " + id);
        if (page == null && size == null) {
            return ResponseEntity.ok(getService().getAllCommentsById(id));
        }
        if (page == null || size == null) {
            log.warn("Only both 'page' and 'size' params are required");
            throw new ValidationException("Only both 'page' and 'size' params are required");
        }
        return ResponseEntity.ok(getService().getPaginatedCommentsById(id, page, size));
    }

    @PostMapping("/{id}/comments/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Добавить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<SightCommentDTO> createComment(@PathVariable Long id,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Добавление отзыва к точке интереса с id = " + id);
        return new ResponseEntity<>(getService().addComment(id, commentDTO), HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}/comments/my/delete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        log.info("Удаление отзыва для точки с id: " + id);
        getService().deleteComment(id);
        return ResponseEntity.ok("Comment was deleted");
    }

    @PutMapping("/{id}/comments/my/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<SightCommentDTO> updateComment(@PathVariable Long id,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Изменение отзыва для точки с id: " + id);
        return ResponseEntity.ok(getService().updateComment(id, commentDTO));
    }

}
