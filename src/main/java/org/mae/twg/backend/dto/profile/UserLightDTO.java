package org.mae.twg.backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.models.auth.User;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Log4j2
public class UserLightDTO implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private String patronymic;

    public UserLightDTO(User user) {
        log.debug("start UserLightDTO constructor");
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.patronymic = user.getPatronymic();
        log.debug("end UserLightDTO constructor");
    }
}
