package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "property_local")
public class PropertyLocal implements Local{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "property_id")
    private Property property;

    public PropertyLocal(@NonNull String title,
                         String description,
                         Property property,
                         Localization localization) {
        this.title = title;
        this.description = description;
        this.localization = localization;
        this.property = property;
    }

    @Override
    public String getString() {
        return title;
    }

    @Override
    public Model getModel() {
        return property;
    }
}
