package org.mae.twg.backend.controllers.business;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AgencyController {
    private final AgencyService agencyService;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(agencyService.getAll(local));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        return ResponseEntity.ok(agencyService.getById(id, local));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody AgencyRequestDTO agencyDTO) throws URISyntaxException {
        AgencyDTO agency = agencyService.create(agencyDTO, local);
        return ResponseEntity
                .created(new URI("business/" + local + "/agencies/" + agency.getId()))
                .body(agency);
    }

    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody AgencyRequestDTO agencyDTO) throws URISyntaxException {
        AgencyDTO hotel = agencyService.addLocal(id, agencyDTO, local);
        return ResponseEntity
                .created(new URI("business/" + local + "/agencies/" + hotel.getId()))
                .body(hotel);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody AgencyRequestDTO agencyDTO) {
        return ResponseEntity.ok(agencyService.updateLocal(id, agencyDTO, local));
    }
}
