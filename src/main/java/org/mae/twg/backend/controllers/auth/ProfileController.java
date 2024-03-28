package org.mae.twg.backend.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.auth.UserDTO;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.services.auth.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Tag(name = "Профиль пользователя")
@Log4j2
public class ProfileController {
    private final UserService userService;
    @ResponseBody
    @Operation(
            summary = "Профиль пользователя",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>")
    )
    @GetMapping("/me")
    public ResponseEntity<UserDTO> currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok((new UserDTO((User) authentication.getPrincipal())));
    }
    @PostMapping("/image/upload")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.USER)")
    @Operation(summary = "Добавить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> uploadImages(MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        log.info("Добавление фотографии к профилю c id = " + id);
        if (image == null) {
            throw new ValidationException("Пустой список фотографий");
        }
        return ResponseEntity.ok(userService.uploadImages(image));
    }

    @DeleteMapping("/image/delete")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.USER)")
    @Operation(summary = "Удалить фотографии",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<?> deleteImages(@RequestBody String image) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        log.info("Delete images from user profile with id = " + id);
        if (image == null) {
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(userService.deleteImages(image));
    }
}
