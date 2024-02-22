package org.mae.twg.backend.controllers.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.business.AgencyDTO;
import org.mae.twg.backend.dto.business.AgencyRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.business.AgencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/business/{local}/agencies")
@Tag(name = "Агенства")
@Log4j2
public class AgencyController {
    private final AgencyService agencyService;

    @GetMapping
    @Operation(summary = "Отдать все агенства",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        log.info("Отдать все агенства");
        return ResponseEntity.ok(agencyService.getAll(local));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отдать агенство по id",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        log.info("Отдать агенство по id");
        return ResponseEntity.ok(agencyService.getById(id, local));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteById(@PathVariable Long id,
                                        @PathVariable Localization local) {
        agencyService.deleteById(id);
        return ResponseEntity.ok("Marked as deleted");
    }

    @PostMapping("/create")
    @Operation(summary = "Создать агенство",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody AgencyRequestDTO agencyDTO) throws URISyntaxException {
        AgencyDTO agency = agencyService.create(agencyDTO, local);
        log.info("Создать агенство");
        return ResponseEntity
                .created(new URI("business/" + local + "/agencies/" + agency.getId()))
                .body(agency);
    }

    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody AgencyRequestDTO agencyDTO) throws URISyntaxException {
        AgencyDTO hotel = agencyService.addLocal(id, agencyDTO, local);
        log.info("Добавить локаль");
        return ResponseEntity
                .created(new URI("business/" + local + "/agencies/" + hotel.getId()))
                .body(hotel);
    }

    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody AgencyRequestDTO agencyDTO) {
        log.info("Обновить локаль");
        return ResponseEntity.ok(agencyService.updateLocal(id, agencyDTO, local));
    }
}
