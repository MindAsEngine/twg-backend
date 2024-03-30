package org.mae.twg.backend.controllers.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.business.CallReqResponseDTO;
import org.mae.twg.backend.dto.business.CallRequestDTO;
import org.mae.twg.backend.services.business.CallRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business/requests/call")
@Tag(name = "Заявка на звонок")
@Log4j2
@RequiredArgsConstructor
public class CallRequestController {
    private final CallRequestService callRequestService;

    @PostMapping("/add")
    @Operation(summary = "Добавить заявку на звонок")
    public ResponseEntity<CallReqResponseDTO> addCall(@RequestBody CallRequestDTO callRequestDTO)  {
        log.info("Добавить заявку на звонок");
        if (callRequestDTO == null) {
            throw new ValidationException("Заявка пустая");
        }
        return ResponseEntity.ok(callRequestService.addRequest(callRequestDTO));
    }

    @GetMapping("/get")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Отдать все заявки агентства")
    public ResponseEntity<List<CallReqResponseDTO>> getCall(@RequestParam (required = false) Long agency_id)  {
        log.info("Отдать все заявки агентства");
        return ResponseEntity.ok(callRequestService.getAll(agency_id));
    }

    @PostMapping("/resolve")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Решить заявку")
    public ResponseEntity<List<CallReqResponseDTO>> resolve(@RequestBody Long request_id)  {
        log.info("Отдать все заявки агентства");
        if (request_id == null) {
            throw new ValidationException("Не передали заявку");
        }
        return ResponseEntity.ok(callRequestService.resolve(request_id));
    }
}
