package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.mae.twg.backend.models.travel.media.HotelMedia;
import org.mae.twg.backend.models.travel.media.ResortMedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hotel_id")
    private Long id;

//    @Column(name = "slug", unique = true)
//    private String slug;
//    TODO: add slug generation

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stars")
    private Stars stars;

    @NonNull
    @Column(name = "city")
    private String city;

    @NonNull
    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @NonNull
    @Column(name = "address")
    private String address;
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

    @OneToMany(mappedBy = "hotel",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HotelMedia> medias = new ArrayList<>();

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
}
