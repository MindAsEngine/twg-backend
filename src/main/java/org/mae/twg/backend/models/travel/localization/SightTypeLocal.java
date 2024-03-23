package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.SightType;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tag_local")
public class SightTypeLocal implements Local {
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
    @JoinColumn(name = "sight_type_id")
    private SightType sightType;

    public SightTypeLocal(@NonNull String name,
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
        return sightType;
    }
}
