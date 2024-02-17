package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.TourMedia;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class TourDTO implements Serializable {
    private Long id;
    private String title;
    private String description;
    private CountryDTO country;
    private Localization localization;
    private List<String> medias;
    private List<HotelDTO> hotels;

    public TourDTO(Tour tour, Localization localization) {
        this.id = tour.getId();
        TourLocal cur_local =
                tour.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Tour "
                                + localization.name() + " localization not found"));
        this.title = cur_local.getTitle();
        this.description = cur_local.getDescription();
        this.medias = tour.getMedias().stream().map(TourMedia::getMediaPath).toList();
        this.hotels = tour.getHotels().stream()
                .map(hotel -> new HotelDTO(hotel, localization))
                .toList();
        this.country = new CountryDTO(tour.getCountry(), localization);
        this.localization = localization;
    }
}
