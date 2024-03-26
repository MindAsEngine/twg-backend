package org.mae.twg.backend.controllers.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.business.AgencyDTO;
import org.mae.twg.backend.dto.business.AgencyRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.business.AgencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business/{local}/agencies")
@Tag(name = "Агенства")
@Log4j2
public class AgencyController extends BaseController<AgencyService, AgencyDTO, AgencyRequestDTO> {
    public AgencyController(AgencyService service) {
        super(service);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Отдать агенство по id")
    public ResponseEntity<AgencyDTO> getById(@PathVariable Long id,
                                     @PathVariable Localization local) {
        log.info("Отдать агенство по id");
        return ResponseEntity.ok(getService().getById(id, local));
    }
}
