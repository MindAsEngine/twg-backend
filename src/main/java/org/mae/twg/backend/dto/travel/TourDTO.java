package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.dto.travel.response.CountryDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.TourMedia;

import java.util.List;

@Data
@AllArgsConstructor
public class TourDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String title;
    private TourType type;
    private String description;
    private CountryDTO country;
    private Localization localization;
    private List<String> medias;
    private List<HotelLightDTO> hotels;
    private Boolean isActive;

    public TourDTO(Tour tour, Localization localization) {
        this.id = tour.getId();
        this.slug = tour.getSlug();
        TourLocal cur_local =
                tour.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Tour "
                                + localization.name() + " localization not found"));
        this.title = cur_local.getTitle();
        this.type = tour.getType();
        this.isActive = tour.getIsActive();
        this.description = cur_local.getDescription();
        this.medias = tour.getMedias().stream().map(TourMedia::getMediaPath).toList();
        this.hotels = tour.getHotels().stream()
                .map(hotel -> HotelLightDTO.getDTO(hotel, localization))
                .toList();
        this.country = CountryDTO.getDTO(tour.getCountry(), localization);
        this.localization = localization;
    }

    static public TourDTO getDTO(Tour tour, Localization localization) {
        if (tour == null || tour.getIsDeleted()) {
            return null;
        }
        return new TourDTO(tour, localization);
    }
}
