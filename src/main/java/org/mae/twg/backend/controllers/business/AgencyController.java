package org.mae.twg.backend.controllers.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.controllers.BaseController;
import org.mae.twg.backend.dto.StringDTO;
import org.mae.twg.backend.dto.business.AgencyDTO;
import org.mae.twg.backend.dto.business.AgencyRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.business.AgencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        log.info("Отдача агентства с id " + id);
        return ResponseEntity.ok(getService().getById(id, local));
    }

    @PostMapping("/{id}/add/agent")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(
            summary = "Избранные туры",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>")
    )
    public ResponseEntity<AgencyDTO> addAgentByUsername(@PathVariable Long id,
                                                      @PathVariable Localization local,
                                                      @RequestBody StringDTO username) {
        log.info("Добавление пользователя " + username + " в агенство с id = " + id);
        return ResponseEntity.ok(getService().addAgent(id, username.getData(), local));
    }
}
