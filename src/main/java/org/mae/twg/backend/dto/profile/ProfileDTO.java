package org.mae.twg.backend.dto.profile;

import lombok.Data;

@Data
public class ProfileDTO {
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String patronymic;
}
