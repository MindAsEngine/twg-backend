package org.mae.twg.backend.controllers.travel;

import org.mae.twg.backend.dto.travel.request.PropertyRequestDTO;
import org.mae.twg.backend.services.travel.PropertyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/properties")
public class PropertyController extends AbstractTravelController<PropertyService, PropertyRequestDTO, PropertyRequestDTO>{
    public PropertyController(PropertyService service) {
        super(service);
    }
}
