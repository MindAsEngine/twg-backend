package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.Tour;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tour_medias")
public class TourMedia {
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
}
