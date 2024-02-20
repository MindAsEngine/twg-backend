package org.mae.twg.backend.controllers.travel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.HotelDTO;
import org.mae.twg.backend.dto.travel.request.HotelLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.HotelRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/travel/{local}/hotels")
public class HotelController {
    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Localization local) {
        return ResponseEntity.ok(hotelService.getAll(local));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        return ResponseEntity.ok(hotelService.getById(id, local));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody HotelRequestDTO hotelDTO) throws URISyntaxException {
        HotelDTO hotel = hotelService.create(hotelDTO, local);
        return ResponseEntity
                .created(new URI("travel/" + local + "/hotels/" + hotel.getId()))
                .body(hotel);
    }

    @PatchMapping("/{id}/locals/add")
    public ResponseEntity<?> addLocal(@PathVariable Long id,
                                      @PathVariable Localization local,
                                      @Valid @RequestBody HotelLocalRequestDTO hotelDTO) throws URISyntaxException {
        HotelDTO hotel = hotelService.addLocal(id, hotelDTO, local);
        return ResponseEntity
                .created(new URI("travel/" + local + "/hotels/" + hotel.getId()))
                .body(hotel);
    }

    @PutMapping("/{id}/locals/update")
    public ResponseEntity<?> updateLocal(@PathVariable Long id,
                                         @PathVariable Localization local,
                                         @Valid @RequestBody HotelLocalRequestDTO hotelDTO) {
        return ResponseEntity.ok(hotelService.updateLocal(id, hotelDTO, local));
    }

    @PutMapping("/{id}/properties/update")
    public ResponseEntity<?> updateProperties(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody List<Long> propertyIds) {
        return ResponseEntity.ok(hotelService.updateProperties(id, propertyIds, local));
    }

    @PutMapping("/{id}/sights/update")
    public ResponseEntity<?> updateSights(@PathVariable Long id,
                                              @PathVariable Localization local,
                                              @Valid @RequestBody List<Long> sightIds) {
        return ResponseEntity.ok(hotelService.updateSights(id, sightIds, local));
    }
}