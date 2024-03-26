package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.locals.ResortLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.ResortLogicDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.ResortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/resorts")
@Tag(name = "Курорты")
@Log4j2
public class ResortController extends BaseTravelController<ResortService, ResortLocalDTO, ResortLocalDTO> {

    public ResortController(ResortService service) {
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

    @PutMapping("/{id}/logic/update")
    @Operation(summary = "Обновление логичсеких данных курорта",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLogicData(@PathVariable Localization local,
                                             @RequestParam Long id,
                                             @RequestBody ResortLogicDTO resortDTO) {
        log.info("Установка курорту с id = " + id + " страны с id = " + resortDTO.getCountryId());
        return ResponseEntity.ok(getService().updateLogicData(id, resortDTO, local));
    }
}
