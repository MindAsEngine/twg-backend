package org.mae.twg.backend.controllers.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.SightDTO;
import org.mae.twg.backend.dto.travel.request.SightRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/sights")
public class SightController {
    private final SightService sightService;
//    private final Logger log = LoggerFactory.getLogger(SightController.class);

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(sightService.getAll(local));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSightById(@PathVariable Long id,
                                          @PathVariable Localization local) {
        SightDTO sightDTO = sightService.getById(id, local);
        return ResponseEntity.ok(sightDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSight(@PathVariable Localization local,
                                         @RequestBody SightRequestDTO sightDTO) throws URISyntaxException {
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
                                      @RequestBody SightRequestDTO sightDTO) {
        SightDTO sight = sightService.addLocal(id, sightDTO, local);
//        TODO: add URI sending
        return new ResponseEntity<>(sight, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @RequestBody SightRequestDTO sightDTO) {
        SightDTO sight = sightService.updateLocal(id, sightDTO, local);
        return ResponseEntity.ok(sight);
    }
}
