package org.mae.twg.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
@RequiredArgsConstructor
@Tag(name = "Проверка запуска")
public class ExampleController {

    @GetMapping
    @Operation(summary = "Отправь ping - получи pong")
    public String start() {
        return "PONG";
    }

}
