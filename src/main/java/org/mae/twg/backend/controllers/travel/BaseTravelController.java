package org.mae.twg.backend.controllers.travel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.controllers.TravelController;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.TravelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
public abstract class BaseTravelController<
        ServiceType extends TravelService<ModelReqDTO, LocalReqDTO>,
        ModelReqDTO, LocalReqDTO>
        implements TravelController<ModelReqDTO, LocalReqDTO> {

    private final ServiceType service;

    protected ServiceType getService() {
        return service;
    }

    @Override
    @GetMapping
    @Operation(summary = "Отдать все сущности",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = false, example = "Bearer <token>")
    )
    public ResponseEntity<?> getAll(@PathVariable Localization local,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size) {
        if (page == null && size == null) {
            return ResponseEntity.ok(service.getAll(local));
        }
        if (page != null && size != null) {
            return ResponseEntity.ok(service.getAllPaged(local, page, size));
        }
        throw new ValidationException("Only both 'page' and 'size' params are required");
    }

    @Override
    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Удалить сущность",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> deleteById(@PathVariable Localization local, @PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok("Marked as deleted");
    }

    @Override
    @PostMapping("/create")
    @Operation(summary = "Создать сущность",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> create(@PathVariable Localization local,
                                    @Valid @RequestBody ModelReqDTO modelDTO) {
        return new ResponseEntity<>(service.create(modelDTO, local),
                HttpStatus.CREATED);
    }

    @Override
    @PatchMapping("/{id}/locals/add")
    @Operation(summary = "Добавить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> addLocal(@PathVariable Localization local,
                                      @PathVariable Long id,
                                      @Valid @RequestBody LocalReqDTO localDTO) {
        return new ResponseEntity<>(service.addLocal(id, localDTO, local),
                HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}/locals/update")
    @Operation(summary = "Обновить локаль",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> updateLocal(@PathVariable Localization local,
                                         @PathVariable Long id,
                                         @Valid @RequestBody LocalReqDTO localDTO) {
        return ResponseEntity.ok(service.updateLocal(id, localDTO, local));

    }

}
