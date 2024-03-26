package org.mae.twg.backend.models.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agency_local")
public class AgencyLocal implements Local {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "contacts",
            columnDefinition = "TEXT")
    private String contacts;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    public AgencyLocal(@NonNull String name,
                       String description,
                       String contacts,
                       String address,
                       Localization localization) {
        this.name = name;
        this.description = description;
        this.contacts = contacts;
        this.address = address;
        this.localization = localization;
    }

    @Override
    public String getString() {
        return name;
    }

    @Override
    public Agency getModel() {
        return agency;
    }
}
