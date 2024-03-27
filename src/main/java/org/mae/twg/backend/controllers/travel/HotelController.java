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

    @PostMapping("/{id}/image/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить обложку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelDTO> uploadImage(@PathVariable Localization local,
                                                @PathVariable Long id,
                                                MultipartFile image) throws IOException {
        log.info("Добавление обложки к отелю");
        if (image == null) {
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
    public ResponseEntity<HotelDTO> deleteImages(@PathVariable Localization local,
                                                 @PathVariable Long id,
                                                 @RequestBody List<String> images) {
        log.info("Delete images from hotel with id = " + id);
        if (images == null) {
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
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            return ResponseEntity.ok(getService().getById(id, local));
        }
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

    @DeleteMapping("/{id}/comments/{commentId}/delete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        log.info("Удаление отзыва с id = " + commentId);
        getService().deleteByCommentId(commentId);
        return ResponseEntity.ok("Comment with id = " + commentId + " marked as deleted");
    }

    @PutMapping("/{id}/comments/{commentId}/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HotelCommentDTO> updateComment(@PathVariable Long commentId,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Изменение отзыва с id = " + commentId);
        return ResponseEntity.ok(getService().updateByCommentId(commentId, commentDTO));
    }
}
