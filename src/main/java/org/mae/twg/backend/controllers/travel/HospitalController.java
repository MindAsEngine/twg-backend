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
import org.mae.twg.backend.dto.travel.request.geo.HospitalGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.HospitalLocalDTO;
import org.mae.twg.backend.dto.travel.response.HospitalDTO;
import org.mae.twg.backend.dto.travel.response.comments.HospitalCommentDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/hospitals")
@Tag(name = "Больницы")
@Log4j2
public class HospitalController extends BaseController<HospitalService, HospitalDTO, HospitalLocalDTO> {

    public HospitalController(HospitalService service) {
        super(service);
    }

    @PostMapping("/{id}/image/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить обложку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HospitalDTO> uploadImage(@PathVariable Localization local,
                                                @PathVariable Long id,
                                                MultipartFile image) throws IOException {
        log.info("Добавление обложки к больнице с id: " + id);
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
    public ResponseEntity<HospitalDTO> uploadImages(@PathVariable Localization local,
                                                 @PathVariable Long id,
                                                 List<MultipartFile> images) throws IOException {
        log.info("Добавление фотографий к больнице с id: " + id);
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
    public ResponseEntity<HospitalDTO> deleteImages(@PathVariable Localization local,
                                                 @PathVariable Long id,
                                                 @RequestBody List<String> images) {
        log.info("Delete images from hospital with id = " + id);
        if (images == null) {
            log.warn("Empty images list");
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(getService().deleteImages(id, local, images));
    }


    @GetMapping("/get")
    @Operation(summary = "Отдать больницу по id")
    public ResponseEntity<HospitalDTO> get(@PathVariable Localization local,
                                        @RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            log.warn("One of id or slug is required");
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            log.info("Отдать больницу с id: " + id);
            return ResponseEntity.ok(getService().getById(id, local));
        }
        log.info("Отдать больницу по slug: " + slug);
        return ResponseEntity.ok(getService().getBySlug(slug, local));
    }

    @PutMapping("/{id}/geo/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновить геоданных больницы",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HospitalDTO> updateGeo(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody HospitalGeoDTO hospitalDTO) {
        log.info("Обновление геоданных данных больнице с id = " + id);
        return ResponseEntity.ok(getService().updateGeoData(id, hospitalDTO, local));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "Получение отзывов")
    public ResponseEntity<List<HospitalCommentDTO>> getComments(@PathVariable Long id,
                                                             @RequestParam(required = false) Integer page,
                                                             @RequestParam(required = false) Integer size) {
        log.info("Получение отзывов к больнице с id = " + id);
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
    public ResponseEntity<HospitalCommentDTO> createComment(@PathVariable Long id,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Добавление отзыва к больнице с id = " + id);
        return new ResponseEntity<>(getService().addComment(id, commentDTO), HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}/comments/my/delete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        log.info("Удаление отзыва для больницы с id: " + id);
        getService().deleteComment(id);
        return ResponseEntity.ok("Comment was deleted");
    }

    @PutMapping("/{id}/comments/my/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Изменить отзыв",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<HospitalCommentDTO> updateComment(@PathVariable Long id,
                                                         @RequestBody CommentDTO commentDTO) {
        log.info("Изменение отзыва для больницы с id: " + id);
        return ResponseEntity.ok(getService().updateComment(id, commentDTO));
    }
}
