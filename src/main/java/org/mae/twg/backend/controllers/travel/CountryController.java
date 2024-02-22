package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.CountryRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel/{local}/countries")
@Tag(name = "Страны")
@Log4j2
public class CountryController {
    private final CountryService propertyService;

    @GetMapping
    @Operation(summary = "Отдать все страны",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать все страны");
        return ResponseEntity.ok(propertyService.getAll(local));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать страну",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody CountryRequestDTO countryDTO) {
        log.info("Создать страну");
        return new ResponseEntity<>(propertyService.create(countryDTO, local),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                    @PathVariable Localization local,
                                    @Valid @RequestBody CountryRequestDTO countryDTO) {
        log.info("Добавить локаль");
        return new ResponseEntity<>(propertyService.addLocal(id, countryDTO, local),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                    @PathVariable Localization local,
                                    @Valid @RequestBody CountryRequestDTO countryDTO) {
        log.info("Обновить локаль");
        return ResponseEntity.ok(propertyService.updateLocal(id, countryDTO, local));
    }
}
