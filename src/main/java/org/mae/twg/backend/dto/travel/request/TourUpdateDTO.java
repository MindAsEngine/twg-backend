package org.mae.twg.backend.dto.travel.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.TourType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TourUpdateDTO implements RequestDTO<Tour> {
    TourType type;
    Long countryId;
    Long agencyId;
    Boolean isBurning;
    Boolean isCustom;
    Boolean isActive;
    LocalDate startDate;
    LocalDate endDate;
}
