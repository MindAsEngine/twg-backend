package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hotels")
public class Hotel implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hotel_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    @Column(name = "slug", unique = true)
    private String slug;

    @OneToMany(mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HotelLocal> locals = new ArrayList<>();

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stars")
    private Stars stars;
//    TODO: add field for map data

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "hotel_properties",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id"))
    private Set<Property> properties = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "hotel_sights",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "sight_id"))
    private Set<Sight> sights = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "hotels")
    private Set<Tour> tours = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resort_id")
    private Resort resort;

    @OneToMany(mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HotelMedia> medias = new ArrayList<>();

    public void addLocal(HotelLocal local) {
        locals.add(local);
        local.setHotel(this);
    }

    public void removeLocal(HotelLocal local) {
        locals.remove(local);
        local.setHotel(null);
    }

    public void addMedia(HotelMedia media) {
        medias.add(media);
        media.setHotel(this);
    }

    public void removeMedia(HotelMedia media) {
        medias.remove(media);
        media.setHotel(null);
    }

    public void addSight(Sight sight) {
        sights.add(sight);
        sight.getHotels().add(this);
    }

    public void removeSight(Sight sight) {
        sights.remove(sight);
        sight.getHotels().remove(this);
    }

    public void addProperty(Property property) {
        properties.add(property);
        property.getHotels().add(this);
    }

    public void removeProperty(Property property) {
        properties.remove(property);
        property.getHotels().remove(this);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<HotelLocal> getLocalizations() {
        return locals;
    }
}
