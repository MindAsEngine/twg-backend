package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.services.admin.ConfigColorService;
import org.mae.twg.backend.services.admin.ConfigDisplayEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/configs/colors")
@RequiredArgsConstructor
@Tag(name = "Динамический конфиг цветов блоков сайта")
@Log4j2
public class ConfigColorController {
    private final ConfigColorService configColorService;
    @Operation(summary = "Получить всю конфигурацию")
    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(configColorService.getAll());
    }
    @Operation(summary = "Обновить конфиг цветов")
    @PutMapping("/put")
    public ResponseEntity<?> putAll(@RequestBody Map<ConfigDisplayEnum, String> config) {
        return ResponseEntity.ok(configColorService.put(config));
    }
}
