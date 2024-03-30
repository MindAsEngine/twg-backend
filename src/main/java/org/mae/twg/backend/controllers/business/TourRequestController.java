package org.mae.twg.backend.controllers.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.business.TourReqResponseDTO;
import org.mae.twg.backend.dto.business.TourRequestDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.business.TourRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business/requests/tour/{local}")
@Tag(name = "Заявка на тур")
@Log4j2
@RequiredArgsConstructor
public class TourRequestController {
    private final TourRequestService tourRequestService;
    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Добавить заявку на тур",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<TourReqResponseDTO> addRequest(@PathVariable Localization local,
                                                         @RequestBody TourRequestDTO tourRequestDTO)  {
        log.info("Добавить заявку на тур");
        if (tourRequestDTO == null) {
            throw new ValidationException("Заявка пустая");
        }
        return ResponseEntity.ok(tourRequestService.addRequest(tourRequestDTO, local));
    }

    @GetMapping("/get")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Отдать все заявки",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<List<TourReqResponseDTO>> getRequests(@PathVariable Localization local)  {
        log.info("Отдать все заявки");
        return ResponseEntity.ok(tourRequestService.getAll(local));
    }

    @PostMapping("/resolve")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Решить заявку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<List<TourReqResponseDTO>> resolve(@PathVariable Localization local,
                                                            @RequestBody Long request_id)  {
        log.info("Отдать все заявки");
        if (request_id == null) {
            throw new ValidationException("Не передали заявку");
        }
        return ResponseEntity.ok(tourRequestService.resolve(request_id, local));
    }
}
