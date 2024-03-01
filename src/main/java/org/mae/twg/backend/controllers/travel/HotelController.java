package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.mae.twg.backend.dto.travel.request.HotelLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.HotelRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel/{local}/hotels")
public class HotelController extends BaseTravelController<HotelService, HotelRequestDTO, HotelLocalRequestDTO> {
    public HotelController(HotelService service) {
        super(service);
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@PathVariable Localization local,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) String slug) {
        if (id == null && slug == null) {
            throw new ValidationException("One of id or slug is required");
        }
        if (id != null) {
            return ResponseEntity.ok(getService().getById(id, local));
        }
        return ResponseEntity.ok(getService().getBySlug(slug, local));
    }

    @PutMapping("/{id}/properties/update")
    public ResponseEntity<?> updateProperties(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody List<Long> propertyIds) {
        return ResponseEntity.ok(getService().updateProperties(id, propertyIds, local));
    }

    @PutMapping("/{id}/sights/update")
    public ResponseEntity<?> updateSights(@PathVariable Long id,
                                          @PathVariable Localization local,
                                          @Valid @RequestBody List<Long> sightIds) {
        return ResponseEntity.ok(getService().updateSights(id, sightIds, local));
    }
}
