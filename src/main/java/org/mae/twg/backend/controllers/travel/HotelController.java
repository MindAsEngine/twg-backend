package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.HotelDTO;
import org.mae.twg.backend.dto.travel.request.HotelLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.HotelRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/hotels")
@Tag(name = "Отели")
@Log4j2
public class HotelController {
    private final HotelService hotelService;

    @GetMapping
    @Operation(summary = "Отдать все отели",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать все отели");
        return ResponseEntity.ok(hotelService.getAll(local));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отдать отель по id",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        log.info("Отдать отель по id");
        return ResponseEntity.ok(hotelService.getById(id, local));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать отель",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody HotelRequestDTO hotelDTO) throws URISyntaxException {
        HotelDTO hotel = hotelService.create(hotelDTO, local);
        log.info("Создать отель");
        return ResponseEntity
                .created(new URI("travel/" + local + "/hotels/" + hotel.getId()))
                .body(hotel);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody HotelLocalRequestDTO hotelDTO) throws URISyntaxException {
        HotelDTO hotel = hotelService.addLocal(id, hotelDTO, local);
        log.info("Добавить локаль");
        return ResponseEntity
                .created(new URI("travel/" + local + "/hotels/" + hotel.getId()))
                .body(hotel);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody HotelLocalRequestDTO hotelDTO) {
        log.info("Обновить локаль");
        return ResponseEntity.ok(hotelService.updateLocal(id, hotelDTO, local));
    }

    @PutMapping("/{id}/properties/update")
    @Operation(summary = "Обновить свойства отеля",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateProperties(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody List<Long> propertyIds) {
        log.info("Обновить свойства отеля");
        return ResponseEntity.ok(hotelService.updateProperties(id, propertyIds, local));
    }

    @PutMapping("/{id}/sights/update")
    @Operation(summary = "Обновить точки интереса",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateSights(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody List<Long> sightIds) {
        log.info("Обновить точки интереса");
        return ResponseEntity.ok(hotelService.updateSights(id, sightIds, local));
    }
}
