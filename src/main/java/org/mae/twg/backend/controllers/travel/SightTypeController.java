package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.travel.request.locals.SightTypeLocalDTO;
import org.mae.twg.backend.dto.travel.response.SightTypeDTO;
import org.mae.twg.backend.services.travel.SightTypeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel/{local}/sight_types")
@Tag(name = "Типы точек интереса")
@Log4j2
public class SightTypeController extends BaseController<SightTypeService, SightTypeDTO, SightTypeLocalDTO> {
    public SightTypeController(SightTypeService service) {
        super(service);
    }
}
