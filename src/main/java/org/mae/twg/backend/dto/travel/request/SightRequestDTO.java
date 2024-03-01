package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
public class SightRequestDTO implements RequestDTO {
    private String name;
    private String description;
    private String address;
}
