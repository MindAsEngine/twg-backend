package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Media;
import org.mae.twg.backend.models.travel.Tour;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tour_medias")
public class TourMedia implements Media<Tour> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tour_media_id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;

    @Override
    public Tour getModel() {
        return tour;
    }
}
