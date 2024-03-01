package org.mae.twg.backend.controllers.travel;

import org.mae.twg.backend.dto.travel.request.CountryRequestDTO;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.services.travel.CountryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel/{local}/countries")
public class CountryController extends BaseTravelController<Country, CountryRequestDTO, CountryRequestDTO> {
    public CountryController(CountryService service) {
        super(service);
    }
}
