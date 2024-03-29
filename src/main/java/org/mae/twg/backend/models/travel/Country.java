package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.localization.CountryLocal;

import java.util.ArrayList;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "countries")
public class Country implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "country_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "country",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CountryLocal> locals = new ArrayList<>();

    @Column(name = "geo_data",
            columnDefinition = "TEXT")
    private String geoData;

    @Column(name = "media_path")
    private String mediaPath;

    @OneToMany(mappedBy = "country",
            cascade = CascadeType.DETACH,
            orphanRemoval = true)
    private List<Tour> tours = new ArrayList<>();

    @OneToMany(mappedBy = "country",
            cascade = CascadeType.DETACH,
            orphanRemoval = true)
    private List<Resort> resorts = new ArrayList<>();

    public void addLocal(CountryLocal local) {
        locals.add(local);
        local.setCountry(this);
    }

    public void removeLocal(CountryLocal local) {
        locals.remove(local);
        local.setCountry(null);
    }

    public void addResort(Resort resort) {
        resorts.add(resort);
        resort.setCountry(this);
    }

    public void removeResort(Resort resort) {
        resorts.remove(resort);
        resort.setCountry(null);
    }

    public void addTour(Tour tour) {
        tours.add(tour);
        tour.setCountry(this);
    }

    public void removeTour(Tour tour) {
        tours.remove(tour);
        tour.setCountry(null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<CountryLocal> getLocalizations() {
        return locals;
    }
}
