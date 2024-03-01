package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.mae.twg.backend.dto.travel.request.TourLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourUpdateDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/travel/{local}/tours")
public class TourController extends BaseTravelController<TourService, TourRequestDTO, TourLocalRequestDTO> {
    public TourController(TourService service) {
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

    @PutMapping("/{id}/resorts/update")
    public ResponseEntity<?> updateResorts(@PathVariable Long id,
                                           @PathVariable Localization local,
                                           @Valid @RequestBody List<Long> resortIds) {
        return ResponseEntity.ok(getService().updateResorts(id, resortIds, local));
    }

    @PutMapping("/{id}/hotels/update")
    public ResponseEntity<?> updateHotels(@PathVariable Long id,
                                          @PathVariable Localization local,
                                          @Valid @RequestBody List<Long> hotelIds) {
        return ResponseEntity.ok(getService().updateHotels(id, hotelIds, local));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateCountryAndAgency(@PathVariable Long id,
                                                    @PathVariable Localization local,
                                                    @Valid @RequestBody TourUpdateDTO tourDTO) {
        return ResponseEntity.ok(getService().update(id, tourDTO, local));
    }
}
