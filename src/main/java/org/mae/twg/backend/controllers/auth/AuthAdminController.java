package org.mae.twg.backend.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.auth.SignInRequest;
import org.mae.twg.backend.dto.auth.SignUpRequest;
import org.mae.twg.backend.services.auth.AuthAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/admins")
@RequiredArgsConstructor
@Tag(name = "Аутентификация администратора")
public class AuthAdminController {
    private final AuthAdminService authAdminService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authAdminService.signUp(request));
    }

    @Operation(summary = "Вход администратора")
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authAdminService.signIn(request));
    }
}