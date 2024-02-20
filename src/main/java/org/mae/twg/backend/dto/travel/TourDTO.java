package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.business.AgencyDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.TourMedia;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TourDTO implements Serializable {
    private Long id;
    private String title;
    private TourType type;
    private String description;
    private CountryDTO country;
    private Localization localization;
    private List<String> medias;
    private List<HotelLightDTO> hotels;
    private List<ResortLightDTO> resorts;
    private AgencyDTO agency;
    private Boolean isBurning;
    private Boolean isCustom;
    private Boolean isActive;
    private LocalDate startDate;
    private LocalDate endDate;

    public TourDTO(Tour tour, Localization localization) {
        this.id = tour.getId();
        TourLocal cur_local =
                tour.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Tour "
                                + localization.name() + " localization not found"));
        this.title = cur_local.getTitle();
        this.type = tour.getType();
        this.isBurning = tour.getIsBurning();
        this.isCustom = tour.getIsCustom();
        this.isActive = tour.getIsActive();
        this.startDate = tour.getStartDate();
        this.endDate = tour.getEndDate();
        this.description = cur_local.getDescription();
        this.medias = tour.getMedias().stream().map(TourMedia::getMediaPath).toList();
        this.hotels = tour.getHotels().stream()
                .map(hotel -> new HotelLightDTO(hotel, localization))
                .toList();
        this.resorts = tour.getResorts().stream()
                .map(resort -> new ResortLightDTO(resort, localization))
                .toList();
        this.country = tour.getCountry() == null ? null : new CountryDTO(tour.getCountry(), localization);
        this.agency = tour.getAgency() == null ? null : new AgencyDTO(tour.getAgency(), localization);
        this.localization = localization;
    }
}
