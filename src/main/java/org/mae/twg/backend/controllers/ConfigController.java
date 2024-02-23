package org.mae.twg.backend.controllers;

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
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
//        log.info("Пользователь зарегистрирован");
        return ResponseEntity.ok(configService.getAll());
    }

    @Operation(summary = "Получить конфиг по ключу")
    @GetMapping("/get/{key}")
    public ResponseEntity<?> get(@PathVariable String key) {
//        log.info("Администратор вошел");
        return ResponseEntity.ok(configService.get(key));
    }

    @Operation(summary = "Получить конфиг по ключу")
    @PostMapping("/put/{key}")
    public ResponseEntity<?> signIn(@PathVariable String key,
                                    @RequestBody String value) {
//        log.info("Администратор вошел");
        return ResponseEntity.ok(configService.put(key, value));
    }
}