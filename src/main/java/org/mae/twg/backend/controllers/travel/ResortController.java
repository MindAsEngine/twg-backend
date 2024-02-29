package org.mae.twg.backend.controllers.travel;

import jakarta.validation.ValidationException;
import org.mae.twg.backend.dto.travel.request.ResortRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.ResortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/resorts")
public class ResortController extends AbstractTravelController<ResortService, ResortRequestDTO, ResortRequestDTO>{

    public ResortController(ResortService service) {
        super(service);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getById(@PathVariable Localization local,
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
}
