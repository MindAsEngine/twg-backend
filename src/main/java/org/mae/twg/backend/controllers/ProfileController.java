package org.mae.twg.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.profile.FavouriteTourDTO;
import org.mae.twg.backend.dto.profile.UserDTO;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.services.auth.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> addToFavourites(@PathVariable Localization local,
                                                  @RequestBody FavouriteTourDTO tourDTO) {
        log.info("Добавление тура в избранное");
        userService.addTourToFavourite(tourDTO);
        return ResponseEntity.ok("Tour was added to favourites");
    }


}
