package org.mae.twg.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.services.admin.ConfigBusinessEnum;

@Data
@AllArgsConstructor
public class ConfigDTO {
    private ConfigBusinessEnum key;
    private String value;
}
