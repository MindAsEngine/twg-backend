package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "country_local")
public class CountryLocal implements Local{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    public CountryLocal(@NonNull String name,
                        String description,
                        Localization localization,
                        Country country) {
        this.name = name;
        this.description = description;
        this.localization = localization;
        this.country = country;
    }

    @Override
    public String getString() {
        return name;
    }

    @Override
    public Model getModel() {
        return country;
    }
}
