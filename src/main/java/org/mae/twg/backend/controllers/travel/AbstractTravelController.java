package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.controllers.TravelController;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.TravelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
public abstract class AbstractTravelController<
        ServiceType extends TravelService<ModelReqDTO, LocalReqDTO>,
        ModelReqDTO, LocalReqDTO>
        implements TravelController<ModelReqDTO, LocalReqDTO> {

    private final ServiceType service;

    protected ServiceType getService() {
        return service;
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(service.getAll(local));
    }

    @Override
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteById(@PathVariable Localization local, @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok("Marked as deleted");
    }

    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody ModelReqDTO modelDTO) {
        return new ResponseEntity<>(service.create(modelDTO, local),
                HttpStatus.CREATED);
    }

    @Override
    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Localization local,
                                      @PathVariable Long id,
                                      @Valid @RequestBody LocalReqDTO localDTO) {
        return new ResponseEntity<>(service.addLocal(id, localDTO, local),
                HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Localization local,
                                         @PathVariable Long id,
                                         @Valid @RequestBody LocalReqDTO localDTO) {
        return ResponseEntity.ok(service.updateLocal(id, localDTO, local));

    }

}
