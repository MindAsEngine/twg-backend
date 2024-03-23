package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "country_local")
public class CountryLocal implements Local {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    public CountryLocal(@NonNull String name,
                        Localization localization) {
        this.name = name;
        this.localization = localization;
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
