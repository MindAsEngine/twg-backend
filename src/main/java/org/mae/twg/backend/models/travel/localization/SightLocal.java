package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sight_local")
public class SightLocal implements Local {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Column(name = "introduction",
            columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sight_id")
    private Sight sight;

    public SightLocal(@NonNull String name,
                      String introduction,
                      String description,
                      String address,
                      Localization local) {
        this.name = name;
        this.introduction = introduction;
        this.description = description;
        this.address = address;
        this.localization = local;
    }

    @Override
    public String getString() {
        return name;
    }

    @Override
    public Model getModel() {
        return sight;
    }
}
