package org.mae.twg.backend.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.auth.UserRole;

@Data
@AllArgsConstructor
@Schema(description = "Профиль пользователя")
@Log4j2
public class UserDTO {
    @Schema(description = "Логин пользователя", example = "Sapipa")
    private String username;
    @Schema(description = "Id telegram", example = "103815051")
    private String telegramId;
    @Schema(description = "Роль пользователя", example = "USER")
    private UserRole role;
    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;
    @Schema(description = "Номер телефона пользователя", example = "88005553535")
    private String phone;
    @Schema(description = "Имя пользователя", example = "Dima")
    private String firstName;
    @Schema(description = "Фамилия пользователя", example = "Garaj")
    private String lastName;
    @Schema(description = "Отчество пользователя", example = "Valerich")
    private String patronymic;
    @Schema(description = "Фотография пользователя", example = "/user/test.png")
    private String media;

    public UserDTO(User user) {
        log.debug("start UserDTO constructor");
        this.username = user.getUsername();
        this.telegramId = user.getTelegramId();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getUserRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.patronymic = user.getPatronymic();
        this.media = user.getMediaPath();
        log.debug("end UserDTO constructor");
    }
}
