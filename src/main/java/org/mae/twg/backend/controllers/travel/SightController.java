package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.SightDTO;
import org.mae.twg.backend.dto.travel.request.SightRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/sights")
public class SightController {
    private final SightService sightService;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(sightService.getAll(local));
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@PathVariable Localization local,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            return ResponseEntity.ok(sightService.getById(id, local));
        }
        return ResponseEntity.ok(sightService.getBySlug(slug, local));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteById(@PathVariable Long id,
                                        @PathVariable Localization local) {
        sightService.deleteById(id);
        return ResponseEntity.ok("Marked as deleted");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSight(@PathVariable Localization local,
                                         @Valid @RequestBody SightRequestDTO sightDTO) throws URISyntaxException {
        SightDTO sight = sightService.create(sightDTO, local);
//        TODO: add URI sending
        return ResponseEntity
                .created(new URI("travel/"+local+"/sights/"+sight.getId()))
                .body(sight);
//        return new ResponseEntity<>(sight, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody SightRequestDTO sightDTO) throws URISyntaxException {
        SightDTO sight = sightService.addLocal(id, sightDTO, local);
//        TODO: add URI sending
        return ResponseEntity
                .created(new URI("travel/"+local+"/sights/"+sight.getId()))
                .body(sight);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody SightRequestDTO sightDTO) {
        SightDTO sight = sightService.updateLocal(id, sightDTO, local);
        return ResponseEntity.ok(sight);
    }
}
