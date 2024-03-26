package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.services.admin.ConfigDisplayEnum;
import org.mae.twg.backend.services.admin.ConfigVisualService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/configs/visual")
@RequiredArgsConstructor
@Tag(name = "Динамический конфиг визуала сайта")
@Log4j2
public class ConfigVisualController {
    private final ConfigVisualService configVisualService;
    @Operation(summary = "Получить всю конфигурацию",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(configVisualService.getAll());
    }
    @Operation(summary = "Обновить конфиг визуала",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    @PutMapping("/put")
    public ResponseEntity<?> putAll(@RequestBody Map<ConfigDisplayEnum, Boolean> config) {
        return ResponseEntity.ok(configVisualService.put(config));
    }
}
