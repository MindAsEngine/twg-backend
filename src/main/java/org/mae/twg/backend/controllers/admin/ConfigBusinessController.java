package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.models.Currency;
import org.mae.twg.backend.services.admin.ConfigBusinessEnum;
import org.mae.twg.backend.services.admin.ConfigBusinessService;
import org.mae.twg.backend.services.admin.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/configs/business")
@RequiredArgsConstructor
@Tag(name = "Динамический конфиг по бизнес логике")
@Log4j2
public class ConfigBusinessController {
    private final ConfigBusinessService configBusinessService;
    private final CurrencyService currencyService;
    @GetMapping("/currency/history")
    @Operation(summary = "Получить всю историю валют")
    public ResponseEntity<?> getHistory(@RequestParam(required = false) Currency currency) {
        if (currency == null) {
            return ResponseEntity.ok(currencyService.getAllCurrencyHistory());
        }
        return ResponseEntity.ok(currencyService.getAllCurrencyHistoryByCurrency(currency));
    }
    @Operation(summary = "Получить всю конфигурацию")
    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(configBusinessService.getAll());
    }

    @Operation(summary = "Получить конфиг по ключу")
    @GetMapping("/{key}")
    public ResponseEntity<?> get(@PathVariable ConfigBusinessEnum key) {
        return ResponseEntity.ok(configBusinessService.get(key));
    }

    @Operation(summary = "Получить конфиг по ключу")
    @PostMapping("/{key}/put")
    public ResponseEntity<?> put(@PathVariable ConfigBusinessEnum key,
                                 @RequestBody String value) {
        return ResponseEntity.ok(configBusinessService.put(key, value));
    }

    @Operation(summary = "Удалить конфиг по ключу")
    @DeleteMapping("/{key}/delete")
    public ResponseEntity<?> delete(@PathVariable ConfigBusinessEnum key) {
        configBusinessService.delete(key);
        return ResponseEntity.ok().body("Config was deleted");
    }
}