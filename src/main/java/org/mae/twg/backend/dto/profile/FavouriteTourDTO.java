package org.mae.twg.backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FavouriteTourDTO implements Serializable {
    private Long tourId;
}
