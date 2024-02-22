package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.TourDTO;
import org.mae.twg.backend.dto.travel.request.TourLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourUpdateDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/tours")
public class TourController {
    private final TourService tourService;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(tourService.getAll(local));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        return ResponseEntity.ok(tourService.getById(id, local));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteById(@PathVariable Long id,
                                        @PathVariable Localization local) {
        tourService.deleteById(id);
        return ResponseEntity.ok("Marked as deleted");
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody TourRequestDTO tourDTO) throws URISyntaxException {
        TourDTO tour = tourService.create(tourDTO, local);
        return ResponseEntity
                .created(new URI("travel/" + local + "/tours/" + tour.getId()))
                .body(tour);
    }

    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody TourLocalRequestDTO tourDTO) throws URISyntaxException {
        TourDTO tour = tourService.addLocal(id, tourDTO, local);
        return ResponseEntity
                .created(new URI("travel/" + local + "/tours/" + tour.getId()))
                .body(tour);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody TourLocalRequestDTO tourDTO) {
        return ResponseEntity.ok(tourService.updateLocal(id, tourDTO, local));
    }

    @PutMapping("/{id}/resorts/update")
    public ResponseEntity<?> updateResorts(@PathVariable Long id,
                                           @PathVariable Localization local,
                                           @Valid @RequestBody List<Long> resortIds) {
        return ResponseEntity.ok(tourService.updateResorts(id, resortIds, local));
    }

    @PutMapping("/{id}/hotels/update")
    public ResponseEntity<?> updateHotels(@PathVariable Long id,
                                          @PathVariable Localization local,
                                          @Valid @RequestBody List<Long> hotelIds) {
        return ResponseEntity.ok(tourService.updateHotels(id, hotelIds, local));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateCountryAndAgency(@PathVariable Long id,
                                                    @PathVariable Localization local,
                                                    @Valid @RequestBody TourUpdateDTO tourDTO) {
        return ResponseEntity.ok(tourService.update(id, tourDTO, local));
    }
}
