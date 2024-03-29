package org.mae.twg.backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.auth.User;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserLightDTO implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String patronymic;

    public UserLightDTO(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.patronymic = user.getPatronymic();
    }
}
