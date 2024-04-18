package org.mae.twg.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.TravelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
public abstract class BaseController<
        ServiceType extends TravelService<ResponseDTO, LocalDTO>,
        ResponseDTO extends ModelDTO, LocalDTO>
        implements Controller<ResponseDTO, LocalDTO> {

    private final ServiceType service;

    protected ServiceType getService() {
        return service;
    }

    @Override
    @GetMapping
    @Operation(summary = "Отдать все сущности")
    public ResponseEntity<List<ResponseDTO>> getAll(@PathVariable Localization local,
                                                    @RequestParam(required = false) Integer page,
                                                    @RequestParam(required = false) Integer size) {
        log.info("Отдать все сущности");
        if (page == null && size == null) {
            return ResponseEntity.ok(service.getAll(local));
        }
        if (page != null && size != null) {
            return ResponseEntity.ok(service.getAllPaged(local, page, size));
        }
        log.warn("Only both 'page' and 'size' params are required");
        throw new ValidationException("Only both 'page' and 'size' params are required");
    }

    @Override
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Удалить сущность",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteById(@PathVariable Localization local, @PathVariable Long id) {
        log.info("Удалить сущность с id: " + id);
        service.deleteById(id);
        return ResponseEntity.ok("Marked as deleted");
    }

    @Override
    @PostMapping("/create")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Создать сущность",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<ResponseDTO> create(@PathVariable Localization local,
                                    @Valid @RequestBody LocalDTO modelDTO) {
        log.info("Создать сущность");
        return new ResponseEntity<>(service.create(modelDTO, local),
                HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/{id}/locals/add")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<ResponseDTO> addLocal(@PathVariable Localization local,
                                      @PathVariable Long id,
                                      @Valid @RequestBody LocalDTO localDTO) {
        log.info("Добавить локаль");
        return new ResponseEntity<>(service.addLocal(id, localDTO, local),
                HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}/locals/update")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.TWG_ADMIN)")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<ResponseDTO> updateLocal(@PathVariable Localization local,
                                         @PathVariable Long id,
                                         @Valid @RequestBody LocalDTO localDTO) {
        log.info("Обновить локаль");
        return ResponseEntity.ok(service.updateLocal(id, localDTO, local));

    }

}
