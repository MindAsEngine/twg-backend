package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.ResortDTO;
import org.mae.twg.backend.dto.travel.request.ResortRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.ResortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/resorts")
@Tag(name = "Курорты")
@Log4j2
public class ResortController {
    private final ResortService resortService;

    @GetMapping
    @Operation(summary = "Отдать все курорты",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать все курорты");
        return ResponseEntity.ok(resortService.getAll(local));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отдать курорт по id",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        log.info("Отдать курорт по id");
        return ResponseEntity.ok(resortService.getById(id, local));
    }

    @PostMapping("/create")
    @Operation(summary = "Создать курорт",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody ResortRequestDTO resortDTO) throws URISyntaxException {
        ResortDTO hotel = resortService.create(resortDTO, local);
        log.info("Создать курорт");
        return ResponseEntity
                .created(new URI("travel/" + local + "/resorts/" + hotel.getId()))
                .body(hotel);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody ResortRequestDTO resortDTO) throws URISyntaxException {
        ResortDTO hotel = resortService.addLocal(id, resortDTO, local);
        log.info("Добавить локаль");
        return ResponseEntity
                .created(new URI("travel/" + local + "/resorts/" + hotel.getId()))
                .body(hotel);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody ResortRequestDTO resortDTO) {
        log.info("Обновить локаль");
        return ResponseEntity.ok(resortService.updateLocal(id, resortDTO, local));
    }
}
