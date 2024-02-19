package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.request.PropertyRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel/{local}/properties")
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(propertyService.getAll(local));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody PropertyRequestDTO propertyDTO) {
        return new ResponseEntity<>(propertyService.create(propertyDTO, local),
                HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                    @PathVariable Localization local,
                                    @Valid @RequestBody PropertyRequestDTO propertyDTO) {
        return new ResponseEntity<>(propertyService.addLocal(id, propertyDTO, local),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                    @PathVariable Localization local,
                                    @Valid @RequestBody PropertyRequestDTO propertyDTO) {
        return ResponseEntity.ok(propertyService.updateLocal(id, propertyDTO, local));
    }
}
