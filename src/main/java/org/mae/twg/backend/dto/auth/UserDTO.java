package org.mae.twg.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.auth.User;

@Data
@AllArgsConstructor
@Schema(description = "Профиль пользователя")
public class UserDTO {
    @Schema(description = "Логин пользователя", example = "Sapipa")
    @Size(min = 5, max = 50, message = "Логин пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Логин пользователя не может быть пустыми")
    private String username;
    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;
    @Schema(description = "Номер телефона пользователя", example = "88005553535")
    @NotBlank(message = "Номер телефона пользователя не может быть пустыми")
    private String phone;
    @Schema(description = "Имя пользователя", example = "Dima")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String firstName;
    @Schema(description = "Фамилия пользователя", example = "Garaj")
    @NotBlank(message = "Фамилия пользователя не может быть пустыми")
    private String lastName;
    @Schema(description = "Отчество пользователя", example = "Valerich")
    private String patronymic;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.patronymic = user.getPatronymic();
    }
}
