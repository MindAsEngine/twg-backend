package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
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
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "country_id")
    private Long id;

    @OneToMany(mappedBy = "country",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CountryLocal> locals = new ArrayList<>();

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;

    @OneToMany(mappedBy = "country",
            cascade = CascadeType.DETACH,
            orphanRemoval = true)
    private List<Tour> tours = new ArrayList<>();

    public void addLocal(CountryLocal local) {
        locals.add(local);
        local.setCountry(this);
    }

    public void removeLocal(CountryLocal local) {
        locals.remove(local);
        local.setCountry(this);
    }
}
