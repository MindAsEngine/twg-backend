package org.mae.twg.backend.controllers.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.admin.ConfigDTO;
import org.mae.twg.backend.models.Currency;
import org.mae.twg.backend.models.CurrencyHistory;
import org.mae.twg.backend.services.admin.ConfigBusinessEnum;
import org.mae.twg.backend.services.admin.ConfigBusinessService;
import org.mae.twg.backend.services.admin.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("@AuthService.hasAccess(@UserRole.GOD)")
@RequestMapping("/admin/configs/business")
@RequiredArgsConstructor
@Tag(name = "Динамический конфиг бизнес логики")
@Log4j2
public class ConfigBusinessController {
    private final ConfigBusinessService configBusinessService;
    private final CurrencyService currencyService;

    @GetMapping("/currency/history")
    @Operation(summary = "Получить всю историю валют",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>"))
    public ResponseEntity<List<CurrencyHistory>> getHistory(@RequestParam(required = false) Currency currency) {
        if (currency == null) {
            log.info("Отдать всю историю валют");
            return ResponseEntity.ok(currencyService.getAllCurrencyHistory());
        }
        log.info("Отдать историю валют по " + currency.name());
        return ResponseEntity.ok(currencyService.getAllCurrencyHistoryByCurrency(currency));
    }

    @Operation(summary = "Получить всю конфигурацию",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )    @GetMapping()
    public ResponseEntity<List<ConfigDTO>> getAll() {
        log.info("Отдать всю конфигурацию");
        return ResponseEntity.ok(configBusinessService.getAll());
    }

    @Operation(summary = "Получить конфиг по ключу",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    @GetMapping("/{key}")
    public ResponseEntity<ConfigDTO> get(@PathVariable ConfigBusinessEnum key) {
        log.info("Отдать конфиг по ключу " + key.name());
        return ResponseEntity.ok(configBusinessService.get(key));
    }

    @Operation(summary = "Положить конфиг по ключу",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    @PostMapping("/{key}/put")
    public ResponseEntity<ConfigDTO> put(@PathVariable ConfigBusinessEnum key,
                                         @RequestBody String value) {
        log.info("Положили конфиг по ключу " + key.name());
        return ResponseEntity.ok(configBusinessService.put(key, value));
    }

    @Operation(summary = "Удалить конфиг по ключу",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    @DeleteMapping("/{key}/delete")
    public ResponseEntity<String> delete(@PathVariable ConfigBusinessEnum key) {
        log.info("Удалил конфиг по ключу " + key.name());
        configBusinessService.delete(key);
        return ResponseEntity.ok().body("Config was deleted");
    }
}