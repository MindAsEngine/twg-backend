package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.locals.PropertyLocalDTO;
import org.mae.twg.backend.dto.travel.response.PropertyDTO;
import org.mae.twg.backend.services.travel.PropertyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/properties")
@Tag(name = "Свойства отеля")
@Log4j2
public class PropertyController extends BaseController<PropertyService, PropertyDTO, PropertyLocalDTO> {
    public PropertyController(PropertyService service) {
        super(service);
    }
}
