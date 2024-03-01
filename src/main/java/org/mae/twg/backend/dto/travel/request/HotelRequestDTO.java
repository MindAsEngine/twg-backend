package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelRequestDTO;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Stars;

import java.util.List;

@Data
@AllArgsConstructor
public class HotelRequestDTO implements ModelRequestDTO<Hotel> {
    private String name;
    private String city;
    private Stars stars;
    private String description;
    private String address;
    private List<Long> propertyIds;
    private List<Long> sightIds;
}
