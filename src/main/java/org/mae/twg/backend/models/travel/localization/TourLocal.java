package org.mae.twg.backend.models.travel.localization;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tour_local")
public class TourLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @NonNull
    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "tour_id")
    private Tour tour;
}
