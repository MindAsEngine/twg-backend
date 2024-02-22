package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.SightDTO;
import org.mae.twg.backend.dto.travel.request.SightRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/sights")
@Tag(name = "Точки интереса")
@Log4j2
public class SightController {
    private final SightService sightService;
//    private final Logger log = LoggerFactory.getLogger(SightController.class);

    @GetMapping
    @Operation(summary = "Отдать точки интереса",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать точки интереса");
        return ResponseEntity.ok(sightService.getAll(local));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отдать точку интереса по id",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getSightById(@PathVariable Long id,
                                          @PathVariable Localization local) {
        SightDTO sightDTO = sightService.getById(id, local);
        log.info("Отдать точку интереса по id");
        return ResponseEntity.ok(sightDTO);
    }

    @PostMapping("/create")
    @Operation(summary = "Создать точку интереса",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> createSight(@PathVariable Localization local,
                                         @Valid @RequestBody SightRequestDTO sightDTO) throws URISyntaxException {
        SightDTO sight = sightService.create(sightDTO, local);
//        TODO: add URI sending
        log.info("Создать точку интереса");
        return ResponseEntity
                .created(new URI("travel/"+local+"/sights/"+sight.getId()))
                .body(sight);
//        return new ResponseEntity<>(sight, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody SightRequestDTO sightDTO) throws URISyntaxException {
        SightDTO sight = sightService.addLocal(id, sightDTO, local);
//        TODO: add URI sending
        log.info("Добавить локаль");
        return ResponseEntity
                .created(new URI("travel/"+local+"/sights/"+sight.getId()))
                .body(sight);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody SightRequestDTO sightDTO) {
        SightDTO sight = sightService.updateLocal(id, sightDTO, local);
        log.info("Обновить локаль");
        return ResponseEntity.ok(sight);
    }
}
