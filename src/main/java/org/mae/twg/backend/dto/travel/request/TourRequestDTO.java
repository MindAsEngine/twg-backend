package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.travel.enums.TourType;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TourRequestDTO {
    String title;
    TourType type;
    String description;
    Long countryId;
    Long agencyId;
    List<Long> hotelIds;
    List<Long> resortIds;
    Boolean isBurning;
    Boolean isCustom;
    Boolean isActive;
    LocalDate startDate;
    LocalDate endDate;
}
