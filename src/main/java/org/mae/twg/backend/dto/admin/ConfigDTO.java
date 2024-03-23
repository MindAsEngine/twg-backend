package org.mae.twg.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.services.ConfigEnum;

@Data
@AllArgsConstructor
public class ConfigDTO {
    private String key;
    private String value;
}
