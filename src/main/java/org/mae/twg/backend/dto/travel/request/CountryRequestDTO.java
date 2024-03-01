package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.models.travel.Country;

@Data
@AllArgsConstructor
public class CountryRequestDTO implements LocalRequestDTO<Country> {
    private String name;
    private String description;
}
