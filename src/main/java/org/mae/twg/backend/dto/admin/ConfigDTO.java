package org.mae.twg.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigDTO {
    private String key;
    private String value;
}
