package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.dto.travel.response.lightDTOs.HospitalLightDTO;
import org.mae.twg.backend.dto.travel.response.lightDTOs.HotelLightDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.TourMedia;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class TourDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String title;
    private TourType type;
    private String introduction;
    private String description;
    private String additional;
    private Double grade;
    private Long commentAmount;
    private CountryDTO country;
    private Localization localization;
    private String header;
    private List<String> medias;
    private List<HotelLightDTO> hotels;
    private HospitalLightDTO hospital;
    private List<TagDTO> tags;
    private Integer duration;
    private Long price;
    private String route;
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
        this.introduction = cur_local.getIntroduction();
        this.additional = cur_local.getAdditional();
        this.type = tour.getType();
        this.isActive = tour.getIsActive();
        this.description = cur_local.getDescription();
        this.header = (tour.getHeader() != null ? tour.getHeader().getMediaPath() : null);
        this.medias = tour.getMedias().stream().map(TourMedia::getMediaPath).toList();
        this.hotels = tour.getHotels().stream()
                .map(hotel -> HotelLightDTO.getDTO(hotel, localization))
                .filter(Objects::nonNull)
                .toList();
        this.tags = tour.getTags().stream()
                .map(tag -> TagDTO.getDTO(tag, localization))
                .filter(Objects::nonNull)
                .toList();
        this.country = CountryDTO.getDTO(tour.getCountry(), localization);
        this.hospital = HospitalLightDTO.getDTO(tour.getHospital(), localization);
        this.price = tour.getPrice();
        this.duration = tour.getDuration();
        this.route = tour.getRoute();
        this.localization = localization;
    }

    static public TourDTO getDTO(Tour tour, Localization localization) {
        if (tour == null || tour.getIsDeleted()) {
            return null;
        }
        return new TourDTO(tour, localization);
    }
}
