package org.mae.twg.backend.controllers.travel;

import jakarta.validation.ValidationException;
import org.mae.twg.backend.dto.travel.request.SightRequestDTO;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.travel.SightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/sights")
public class SightController extends BaseTravelController<Sight, SightRequestDTO, SightRequestDTO> {

    public SightController(SightService service) {
        super(service);
    }

    @Override
    protected SightService getService() {
        return (SightService) super.getService();
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
}
