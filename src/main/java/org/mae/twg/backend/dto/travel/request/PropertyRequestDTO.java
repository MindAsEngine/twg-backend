package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.models.travel.Property;

@Data
@AllArgsConstructor
public class PropertyRequestDTO implements LocalRequestDTO<Property> {
    private String title;
    private String description;
}
