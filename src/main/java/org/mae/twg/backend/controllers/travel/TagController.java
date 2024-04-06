package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.locals.TagLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.TagLogicDTO;
import org.mae.twg.backend.dto.travel.response.TagDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/tags")
@Tag(name = "Теги туров")
@Log4j2
public class TagController extends BaseController<TagService, TagDTO, TagLocalDTO> {

    public TagController(TagService service) {
        super(service);
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать тег по id")
    public ResponseEntity<TagDTO> getById(@PathVariable Localization local,
                                     @RequestParam Long id) {
        log.info("Отдать логические данные точки интереса с id: " + id);
        return ResponseEntity.ok(getService().getById(id, local));
    }

    @PutMapping("/{id}/logic/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновление логичсеких данных тега",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TagDTO> updateLogicData(@PathVariable Localization local,
                                                    @PathVariable Long id,
                                                    @RequestBody TagLogicDTO tagDTO) {
        log.info("Изменение логических данных точки интереса с id = " + id);
        return ResponseEntity.ok(getService().updateLogic(id, tagDTO, local));
    }
}
