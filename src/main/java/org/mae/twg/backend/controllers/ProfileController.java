package org.mae.twg.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.profile.FavouriteTourDTO;
import org.mae.twg.backend.dto.profile.UserDTO;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.auth.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<UserDTO> uploadImages(MultipartFile image) throws IOException {
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
    public ResponseEntity<UserDTO> deleteImages(@RequestBody String image) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        log.info("Delete images from user profile with id = " + id);
        if (image == null) {
            throw new ValidationException("Empty images list");
        }
        return ResponseEntity.ok(userService.deleteImages(image));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@AuthService.hasAccess(@UserRole.USER)")
    @Operation(summary = "Удалить пользователя",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "JWT токен", required = true, example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();
        log.info("Delete user with username = " + username);
        userService.deleteByUsername(username);
        return ResponseEntity.ok("User was deleted");
    }

    @GetMapping("/{local}/favourites/get")
    @Operation(
            summary = "Избранные туры",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>")
    )
    public ResponseEntity<List<TourDTO>> getFavourites(@PathVariable Localization local) {
        log.info("Получение избранных туров");
        return ResponseEntity.ok(userService.getFavouriteTours(local));
    }

    @PostMapping("/{local}/favourites/add")
    @Operation(
            summary = "Избранные туры",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>")
    )
    public ResponseEntity<String> addToFavourites(@RequestBody FavouriteTourDTO tourDTO) {
        log.info("Добавление тура в избранное");
        userService.addTourToFavourite(tourDTO);
        return ResponseEntity.ok("Tour with id = " + tourDTO.getTourId() + " was added to favourites");
    }

    @GetMapping("/{local}/favourites/check")
    @Operation(
            summary = "Избранные туры",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>")
    )
    public ResponseEntity<Boolean> checkInFavourites(@RequestParam Long tourId) {
        log.info("Проверка тура в избранном");
        return ResponseEntity.ok(userService.checkInFavourite(tourId));
    }

    @DeleteMapping("/{local}/favourites/delete")
    @Operation(
            summary = "Избранные туры",
            parameters = @Parameter(in = ParameterIn.HEADER,
                    name = "Authorization",
                    description = "JWT токен",
                    required = true,
                    example = "Bearer <token>")
    )
    public ResponseEntity<String> deleteFromFavourites(@RequestBody FavouriteTourDTO tourDTO) {
        log.info("Удаление тура из избранного");
        userService.deleteTourFromFavourite(tourDTO);
        return ResponseEntity.ok("Tour with id = " + tourDTO.getTourId() + " was deleted from favourites");
    }
}
