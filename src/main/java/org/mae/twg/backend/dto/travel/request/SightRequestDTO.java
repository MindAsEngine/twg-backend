package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.models.travel.Sight;

@Data
@AllArgsConstructor
public class SightRequestDTO implements LocalRequestDTO<Sight> {
    private String name;
    private String description;
    private String address;
}
