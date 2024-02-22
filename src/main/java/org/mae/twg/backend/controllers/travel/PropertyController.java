package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.PropertyRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel/{local}/properties")
@Tag(name = "Свойства отеля")
@Log4j2
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping
    @Operation(summary = "Отдать все свойства отелей",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать все свойства отелей");
        return ResponseEntity.ok(propertyService.getAll(local));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать свойство отеля",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody PropertyRequestDTO propertyDTO) {
        log.info("Создать свойство отеля");
        return new ResponseEntity<>(propertyService.create(propertyDTO, local),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                    @PathVariable Localization local,
                                    @Valid @RequestBody PropertyRequestDTO propertyDTO) {
        log.info("Добавить локаль");
        return new ResponseEntity<>(propertyService.addLocal(id, propertyDTO, local),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                    @PathVariable Localization local,
                                    @Valid @RequestBody PropertyRequestDTO propertyDTO) {
        log.info("Обновить локаль");
        return ResponseEntity.ok(propertyService.updateLocal(id, propertyDTO, local));
    }
}
