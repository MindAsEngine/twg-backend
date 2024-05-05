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
import org.springframework.security.core.context.SecurityContextHolder;
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
        log.info("Добавить заявку на тур с id: " + tourRequestDTO.getTourId());
        if (tourRequestDTO == null) {
            log.warn("Заявка на тур пустая");
            throw new ValidationException("Заявка пустая");
        }
        return ResponseEntity.ok(tourRequestService.addRequest(tourRequestDTO, local));
    }

    @GetMapping("/get")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.AGENT)")
    @Operation(summary = "Отдать все заявки агентству",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<List<TourReqResponseDTO>> getRequests(@PathVariable Localization local,
                                                                @RequestParam (required = false) Long agencyId)  {
        log.info("Отдать все заявки по агентству");
        return ResponseEntity.ok(tourRequestService.getAll(agencyId, null, null, local));
    }

    @GetMapping("/getMy")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.USER)")
    @Operation(summary = "Отдать все заявки авторизованного пользователя",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<List<TourReqResponseDTO>> getRequestsByUser(@PathVariable Localization local,
                                                                      @RequestParam (required = false, defaultValue = "false") Boolean isAgent)  {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Отдать все заявки авторизованного пользователя: " + username);
        return ResponseEntity.ok(tourRequestService.getAll(null, username, isAgent, local));
    }


    @PostMapping("/set-agent")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.AGENT)")
    @Operation(summary = "Установать агента заявке")
    public ResponseEntity<TourReqResponseDTO> setAgent(@PathVariable Localization local,
                                                       @RequestBody Long requestId)  {
        log.info("Установать агента заявке на тур с id: " + requestId);
        if (requestId == null) {
            log.warn("Не передали заявку на звонок");
            throw new ValidationException("Не передали заявку");
        }
        return ResponseEntity.ok(tourRequestService.setAgent(requestId, local));
    }

    @PostMapping("/resolve")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.AGENT)")
    @Operation(summary = "Решить заявку",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<List<TourReqResponseDTO>> resolve(@PathVariable Localization local,
                                                            @RequestBody Long requestId)  {
        log.info("Решить заявку на тур с id: " + requestId);
        if (requestId == null) {
            log.warn("Не передали заявку на тур");
            throw new ValidationException("Не передали заявку");
        }
        return ResponseEntity.ok(tourRequestService.resolve(requestId, local));
    }
}
