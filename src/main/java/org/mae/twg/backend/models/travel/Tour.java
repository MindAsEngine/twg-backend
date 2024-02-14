package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.TourMedia;

import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tour_id")
    private Long id;

//    @Column(name = "slug", unique = true)
//    private String slug;
//    TODO: add slug generation

    @OneToMany(mappedBy = "tour",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TourLocal> locals = new ArrayList<>();

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TourType type;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tour_hotels",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_id"))
    private Set<Hotel> hotels = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tour_resorts",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "resort_id"))
    private Set<Resort> resorts = new HashSet<>();

    @NonNull
    @Column(name = "is_burning")
    private Boolean isBurning;

    @NonNull
    @Column(name = "is_custom")
    private Boolean isCustom;

    @NonNull
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @NonNull
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @NonNull
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "tour",
            cascade = CascadeType.ALL)
    private List<TourMedia> medias = new ArrayList<>();

    public void addLocal(TourLocal local) {
        locals.add(local);
        local.setTour(this);
    }

    public void removeLocal(TourLocal local) {
        locals.remove(local);
        local.setTour(null);
    }

    public void addMedia(TourMedia media) {
        medias.add(media);
        media.setTour(this);
    }

    public void removeMedia(TourMedia media) {
        medias.remove(media);
        media.setTour(null);
    }

    public void addHotel(Hotel hotel) {
        hotels.add(hotel);
        hotel.getTours().add(this);
    }

    public void removeHotel(Hotel hotel) {
        hotels.remove(hotel);
        hotel.getTours().remove(this);
    }

    public void addResort(Resort resort) {
        resorts.add(resort);
        resort.getTours().add(this);
    }

    public void removeResort(Resort resort) {
        resorts.remove(resort);
        resort.getTours().remove(this);
    }
}