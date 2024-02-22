package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.PropertyRequestDTO;
import org.mae.twg.backend.services.travel.PropertyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/properties")
@Tag(name = "Свойства отеля")
@Log4j2
public class PropertyController extends BaseTravelController<PropertyService, PropertyRequestDTO, PropertyRequestDTO> {
    public PropertyController(PropertyService service) {
        super(service);
    }
}
