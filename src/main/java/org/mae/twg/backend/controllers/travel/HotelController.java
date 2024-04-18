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
import org.mae.twg.backend.dto.travel.request.geo.HotelGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.HotelLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.HotelLogicDTO;
import org.mae.twg.backend.dto.travel.response.HotelDTO;
import org.mae.twg.backend.dto.travel.response.comments.HotelCommentDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/hotels")
@Tag(name = "Отели")
@Log4j2
public class HotelController extends BaseController<HotelService, HotelDTO, HotelLocalDTO> {

    public HotelController(HotelService service) {
        super(service);
    }

    private void validatePageable(Integer page, Integer size) {
        if (page != null && size == null || page == null && size != null) {
            log.warn("Only both 'page' and 'size' params required");
            throw new ValidationException("Only both 'page' and 'size' params required");
        }
    }

    @GetMapping("/find/geo")
    @Operation(summary = "Отели по координатам")
    public ResponseEntity<List<HotelDTO>> findByGeo(@PathVariable Localization local,
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

    @GetMapping("/find/filters")
    @Operation(summary = "Получение отелей по фильтрам")
    public ResponseEntity<List<HotelDTO>> getByFilters(@PathVariable Localization local,
                                                         @RequestParam(required = false) List<Long> resortIds,
                                                         @RequestParam(required = false) List<Long> countryIds,
                                                         @RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer size) {
        validatePageable(page, size);
        if (resortIds == null) {
            log.warn("resortIds is empty");
            resortIds = List.of();
        }
        if (countryIds == null) {
            log.warn("countryIds is empty");
            countryIds = List.of();
        }
        log.info("Get hotels by filters:\nresortIds = " + resortIds + "\ncountryIds = " + countryIds);
        return ResponseEntity.ok(getService().getByFilters(resortIds, countryIds, local, page, size));
    }

    @PostMapping("/{id}/header/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить обложку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelDTO> uploadImage(@PathVariable Localization local,
                                                @PathVariable Long id,
                                                MultipartFile image) throws IOException {
        log.info("Добавление обложки к отелю с id: " + id);
        if (image == null) {
            log.warn("Нет фотографии");
            throw new ValidationException("Нет фотографии");
        }
        return ResponseEntity.ok(getService().uploadImage(id, local, image));
    }

    @PostMapping("/{id}/images/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelDTO> uploadImages(@PathVariable Localization local,
                                                 @PathVariable Long id,
                                                 List<MultipartFile> images) throws IOException {
        log.info("Добавление фотографий к отелю с id: " + id);
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
    public ResponseEntity<HotelDTO> deleteImages(@PathVariable Localization local,
                                                 @PathVariable Long id,
                                                 @RequestBody List<String> images) {
        log.info("Delete images from hotel with id = " + id);
        if (images == null) {
            log.warn("Empty images list");
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(getService().deleteImages(id, local, images));
    }


    @GetMapping("/get")
    @Operation(summary = "Отдать отель по id")
    public ResponseEntity<HotelDTO> get(@PathVariable Localization local,
                                        @RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            log.warn("One of id or slug is required");
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            log.info("Отдать отель по id: " + id);
            return ResponseEntity.ok(getService().getById(id, local));
        }
        log.info("Отдать отель по slug: " + slug);
        return ResponseEntity.ok(getService().getBySlug(slug, local));
    }

    @PutMapping("/{id}/logic/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновить логических данных отеля",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelDTO> updateLogic(@PathVariable Long id,
                                                @PathVariable Localization local,
                                                @Valid @RequestBody HotelLogicDTO hotelDTO) {
        log.info("Обновление логических данных отелю с id = " + id);
        return ResponseEntity.ok(getService().updateLogicData(id, hotelDTO, local));
    }

    @PutMapping("/{id}/geo/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновить геоданных отеля",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelDTO> updateGeo(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody HotelGeoDTO hotelDTO) {
        log.info("Обновление геоданных данных отелю с id = " + id);
        return ResponseEntity.ok(getService().updateGeoData(id, hotelDTO, local));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение отзывов")
    public ResponseEntity<List<HotelCommentDTO>> getComments(@PathVariable Long id,
                                                             @RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer size) {
        log.info("Получение отзывов к отелю с id = " + id);
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
    public ResponseEntity<HotelCommentDTO> createComment(@PathVariable Long id,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Добавление отзыва к отелю с id = " + id);
        return new ResponseEntity<>(getService().addComment(id, commentDTO), HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}/comments/my/delete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        log.info("Удаление отзыва для отеля с id: " + id);
        getService().deleteComment(id);
        return ResponseEntity.ok("Comment was deleted");
    }

    @PutMapping("/{id}/comments/my/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelCommentDTO> updateComment(@PathVariable Long id,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Изменение отзыва для отеля с id: " + id);
        return ResponseEntity.ok(getService().updateComment(id, commentDTO));
    }
}
