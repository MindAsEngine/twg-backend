package org.mae.twg.backend.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.auth.SignInRequest;
import org.mae.twg.backend.dto.auth.SignUpRequest;
import org.mae.twg.backend.dto.auth.TokenRefreshRequest;
import org.mae.twg.backend.services.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Tag(name = "Аутентификация пользователя")
@Log4j2
public class AuthUserController {
    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {
        log.info("Регистрация пользователя");
        return ResponseEntity.ok(authService.signUp(request));
    }

    @Operation(summary = "Вход пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        log.info("Вход пользователя");
        return ResponseEntity.ok(authService.signIn(request));
    }

    @Operation(summary = "Обновление токена доступа")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        log.info("Обновление токена");
        return ResponseEntity.ok(authService.refreshToken(request));
    }


}