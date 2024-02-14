package org.mae.twg.backend.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.SignInRequest;
import org.mae.twg.backend.dto.SignUpRequest;
import org.mae.twg.backend.services.auth.AuthUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Tag(name = "Аутентификация пользователя")
public class AuthUserController {
    private final AuthUserService authUserService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authUserService.signUp(request));
    }

    @Operation(summary = "Вход пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authUserService.signIn(request));
    }
}