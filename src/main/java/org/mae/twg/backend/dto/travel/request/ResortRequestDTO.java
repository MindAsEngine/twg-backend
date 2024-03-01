package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.models.travel.Resort;

@Data
@AllArgsConstructor
public class ResortRequestDTO implements LocalRequestDTO<Resort> {
    private String name;
    private String description;
}
