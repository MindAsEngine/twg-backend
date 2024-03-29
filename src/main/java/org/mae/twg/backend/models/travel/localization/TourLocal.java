package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tour_local")
public class TourLocal implements Local {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "introduction",
            columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Column(name = "additional",
            columnDefinition = "TEXT")
    private String additional;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;

    public TourLocal(@NonNull String title,
                     String introduction,
                     String description,
                     String additional,
                     Localization localization) {
        this.title = title;
        this.introduction = introduction;
        this.description = description;
        this.additional = additional;
        this.localization = localization;
    }

    @Override
    public String getString() {
        return title;
    }

    @Override
    public Model getModel() {
        return tour;
    }
}
