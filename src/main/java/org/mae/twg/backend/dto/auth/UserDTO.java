package org.mae.twg.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.auth.UserRole;

@Data
@AllArgsConstructor
@Schema(description = "Профиль пользователя")
public class UserDTO {
    @Schema(description = "Логин пользователя", example = "Sapipa")
    private String username;
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
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getUserRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.patronymic = user.getPatronymic();
        this.media = user.getMediaPath();
    }
}
