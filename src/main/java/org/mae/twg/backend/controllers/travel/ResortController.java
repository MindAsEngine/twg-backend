package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.locals.ResortLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.ResortLogicDTO;
import org.mae.twg.backend.dto.travel.response.ResortDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.ResortService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel/{local}/resorts")
@Tag(name = "Курорты")
@Log4j2
public class ResortController extends BaseController<ResortService, ResortDTO, ResortLocalDTO> {

    public ResortController(ResortService service) {
        super(service);
    }

    private void validatePageable(Integer page, Integer size) {
        if (page != null && size == null || page == null && size != null) {
            log.warn("Only both 'page' and 'size' params required");
            throw new ValidationException("Only both 'page' and 'size' params required");
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать курорт по id")
    public ResponseEntity<ResortDTO> getById(@PathVariable Localization local,
                                     @RequestParam Long id) {
        log.info("Отдать курорт по id: " + id);
        return ResponseEntity.ok(getService().getById(id, local));
    }

    @GetMapping("/find/filters")
    @Operation(summary = "Получение курортов по фильтрам")
    public ResponseEntity<List<ResortDTO>> getByFilters(@PathVariable Localization local,
                                                       @RequestParam(required = false) List<Long> countryIds,
                                                       @RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer size) {
        validatePageable(page, size);
        if (countryIds == null) {
            log.warn("countryIds is empty");
            countryIds = List.of();
        }
        log.info("Get resorts by filters: countryIds = " + countryIds);
        return ResponseEntity.ok(getService().getByFilters(countryIds, local, page, size));
    }

    @PutMapping("/{id}/logic/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновление логичсеких данных курорта",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<ResortDTO> updateLogicData(@PathVariable Localization local,
                                             @RequestParam Long id,
                                             @RequestBody ResortLogicDTO resortDTO) {
        log.info("Установка курорту с id = " + id + " страны с id = " + resortDTO.getCountryId());
        return ResponseEntity.ok(getService().updateLogicData(id, resortDTO, local));
    }
}
