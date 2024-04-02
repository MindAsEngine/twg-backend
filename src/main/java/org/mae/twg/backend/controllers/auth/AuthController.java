package org.mae.twg.backend.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.auth.*;
import org.mae.twg.backend.dto.profile.UserDTO;
import org.mae.twg.backend.models.auth.UserRole;
import org.mae.twg.backend.services.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Tag(name = "Аутентификация пользователя")
@Log4j2
public class AuthController {
    private final AuthService authService;
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        log.info("Регистрация пользователя");
        return ResponseEntity.ok(authService.signUp(request));
    }

    @Operation(summary = "Вход пользователя")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        log.info("Вход пользователя");
        return ResponseEntity.ok(authService.signIn(request));
    }

    @Operation(summary = "Обновление токена доступа")
    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
        log.info("Обновление токена");
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(
            summary = "Выход",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>"))
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("User was logged out");
    }

    @Operation(
            summary = "Выход",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>"))
    @PreAuthorize("@AuthService.hasAccess(UserRole.TWG_ADMIN)")
    @PostMapping("/create/agent")
    public ResponseEntity<UserDTO> createAgent(@RequestBody SignUpRequest request) {
        log.info("Создание агента");
        return ResponseEntity.ok(authService.addUserWithRole(request, UserRole.AGENT));
    }

    @Operation(
            summary = "Выход",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>"))
    @PreAuthorize("@AuthService.hasAccess(UserRole.GOD)")
    @PostMapping("/create/admin")
    public ResponseEntity<UserDTO> createAdmin(@RequestBody SignUpRequest request) {
        log.info("Создание админа");
        return ResponseEntity.ok(authService.addUserWithRole(request, UserRole.TWG_ADMIN));
    }

    @Operation(
            summary = "Выход",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>"))
    @PreAuthorize("@AuthService.hasAccess(UserRole.GOD)")
    @PostMapping("/delete")
    public ResponseEntity<String> deleteUserByUsername(@RequestBody UserDeleteDTO request) {
        log.info("Удаление пользователя");
        authService.deleteByUsername(request.getUsername());
        return ResponseEntity.ok("User marked as disabled");
    }

}
