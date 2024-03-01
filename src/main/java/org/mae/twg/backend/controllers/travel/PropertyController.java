package org.mae.twg.backend.controllers.travel;

import org.mae.twg.backend.dto.travel.request.PropertyRequestDTO;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.services.travel.PropertyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/properties")
public class PropertyController extends BaseTravelController<Property, PropertyRequestDTO, PropertyRequestDTO> {
    public PropertyController(PropertyService service) {
        super(service);
    }
}
