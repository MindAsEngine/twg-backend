package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.CountryRequestDTO;
import org.mae.twg.backend.services.travel.CountryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/countries")
@Tag(name = "Страны")
@Log4j2
public class CountryController extends BaseTravelController<CountryService, CountryRequestDTO, CountryRequestDTO> {
    public CountryController(CountryService service) {
        super(service);
    }
}
