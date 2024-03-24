package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.locals.TagLocalDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/tags")
@Tag(name = "Теги туров")
@Log4j2
public class TagController extends BaseTravelController<TagService, TagLocalDTO, TagLocalDTO> {

    public TagController(TagService service) {
        super(service);
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать курорт по id",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getById(@PathVariable Localization local,
                                     @RequestParam Long id) {
        return ResponseEntity.ok(getService().getById(id, local));
    }
}
