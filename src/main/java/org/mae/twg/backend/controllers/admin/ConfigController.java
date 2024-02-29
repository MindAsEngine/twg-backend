package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.services.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/configs")
@RequiredArgsConstructor
@Tag(name = "Динамический конфиг")
@Log4j2
public class ConfigController {
    private final ConfigService configService;

    @Operation(summary = "Получить всю конфигурацию")
    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(configService.getAll());
    }

    @Operation(summary = "Получить конфиг по ключу")
    @GetMapping("/{key}")
    public ResponseEntity<?> get(@PathVariable String key) {
        return ResponseEntity.ok(configService.get(key));
    }

    @Operation(summary = "Получить конфиг по ключу")
    @PostMapping("/{key}/put")
    public ResponseEntity<?> put(@PathVariable String key,
                                 @RequestBody String value) {
        return ResponseEntity.ok(configService.put(key, value));
    }

    @Operation(summary = "Удалить конфиг по ключу")
    @DeleteMapping("/{key}/delete")
    public ResponseEntity<?> delete(@PathVariable String key) {
        configService.delete(key);
        return ResponseEntity.ok().body("Config was deleted");
    }
}