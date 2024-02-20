package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ResortController {
    private final ResortService resortService;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(resortService.getAll(local));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        return ResponseEntity.ok(resortService.getById(id, local));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody ResortRequestDTO resortDTO) throws URISyntaxException {
        ResortDTO hotel = resortService.create(resortDTO, local);
        return ResponseEntity
                .created(new URI("travel/" + local + "/resorts/" + hotel.getId()))
                .body(hotel);
    }

    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody ResortRequestDTO resortDTO) throws URISyntaxException {
        ResortDTO hotel = resortService.addLocal(id, resortDTO, local);
        return ResponseEntity
                .created(new URI("travel/" + local + "/resorts/" + hotel.getId()))
                .body(hotel);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody ResortRequestDTO resortDTO) {
        return ResponseEntity.ok(resortService.updateLocal(id, resortDTO, local));
    }
}
