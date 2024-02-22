package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/tours")
@Tag(name = "Туры")
@Log4j2
public class TourController {
    private final TourService tourService;

    @GetMapping
    @Operation(summary = "Отдать все туры",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать все туры");
        return ResponseEntity.ok(tourService.getAll(local));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отдать тур по id",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        log.info("Отдать тур по id");
        return ResponseEntity.ok(tourService.getById(id, local));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать тур",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody TourRequestDTO tourDTO) throws URISyntaxException {
        TourDTO tour = tourService.create(tourDTO, local);
        log.info("Создать тур");
        return ResponseEntity
                .created(new URI("travel/" + local + "/tours/" + tour.getId()))
                .body(tour);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль туру",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody TourLocalRequestDTO tourDTO) throws URISyntaxException {
        TourDTO tour = tourService.addLocal(id, tourDTO, local);
        log.info("Добавить локаль туру");
        return ResponseEntity
                .created(new URI("travel/" + local + "/tours/" + tour.getId()))
                .body(tour);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить тур",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody TourLocalRequestDTO tourDTO) {
        log.info("Обновить тур");
        return ResponseEntity.ok(tourService.updateLocal(id, tourDTO, local));
    }

    @PutMapping("/{id}/resorts/update")
    @Operation(summary = "Обновить курорт",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateResorts(@PathVariable Long id,
                                           @PathVariable Localization local,
                                           @Valid @RequestBody List<Long> resortIds) {
        log.info("Обновить курорт");
        return ResponseEntity.ok(tourService.updateResorts(id, resortIds, local));
    }

    @PutMapping("/{id}/hotels/update")
    @Operation(summary = "Обновить отель",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateHotels(@PathVariable Long id,
                                          @PathVariable Localization local,
                                          @Valid @RequestBody List<Long> hotelIds) {
        log.info("Обновить отель");
        return ResponseEntity.ok(tourService.updateHotels(id, hotelIds, local));
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Обновить страну и агентство",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateCountryAndAgency(@PathVariable Long id,
                                                    @PathVariable Localization local,
                                                    @Valid @RequestBody TourUpdateDTO tourDTO) {
        log.info("Обновить страну и агентство");
        return ResponseEntity.ok(tourService.update(id, tourDTO, local));
    }
}
