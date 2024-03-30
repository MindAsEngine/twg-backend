package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.Hospital;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospital_local")
public class HospitalLocal implements Local {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

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
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    public HospitalLocal(@NonNull String name,
                         String city,
                         String introduction,
                         String description,
                         String address,
                         Localization localization) {
        this.name = name;
        this.city = city;
        this.introduction = introduction;
        this.description = description;
        this.address = address;
        this.localization = localization;
    }

    @Override
    public String getString() {
        return name;
    }

    @Override
    public Model getModel() {
        return hospital;
    }
}
