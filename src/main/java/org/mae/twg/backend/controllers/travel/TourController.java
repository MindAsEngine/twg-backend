package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.TourDTO;
import org.mae.twg.backend.dto.travel.request.TourLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourUpdateDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/tours")
@Tag(name = "Туры")
@Log4j2
public class TourController extends BaseTravelController<TourService, TourRequestDTO, TourLocalRequestDTO> {
    public TourController(TourService service) {
        super(service);
    }

    @GetMapping("/get")
    @Operation(summary = "Отдать тур по id или slug",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> get(@PathVariable Localization local,
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

    @PutMapping("/{id}/resorts/update")
    @Operation(summary = "Обновить курорт",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateResorts(@PathVariable Long id,
                                           @PathVariable Localization local,
                                           @Valid @RequestBody List<Long> resortIds) {
        log.info("Обновить курорт");
        return ResponseEntity.ok(getService().updateResorts(id, resortIds, local));
    }

    @PutMapping("/{id}/hotels/update")
    @Operation(summary = "Обновить отель",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateHotels(@PathVariable Long id,
                                          @PathVariable Localization local,
                                          @Valid @RequestBody List<Long> hotelIds) {
        log.info("Обновить отель");
        return ResponseEntity.ok(getService().updateHotels(id, hotelIds, local));
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Обновить страну и агентство",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateCountryAndAgency(@PathVariable Long id,
                                                    @PathVariable Localization local,
                                                    @Valid @RequestBody TourUpdateDTO tourDTO) {
        log.info("Обновить страну и агентство");
        return ResponseEntity.ok(getService().update(id, tourDTO, local));
    }
}
