package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.LocalRequestDTO;
import org.mae.twg.backend.models.travel.Hotel;

@Data
@AllArgsConstructor
public class HotelLocalRequestDTO implements LocalRequestDTO<Hotel> {
    private String name;
    private String city;
    private String description;
    private String address;
}
