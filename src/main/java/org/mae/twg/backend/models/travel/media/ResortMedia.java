package org.mae.twg.backend.models.travel.media;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.Resort;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resort_medias")
public class ResortMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "resort_media_id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resort_id", nullable = false)
    private Resort resort;

    @NonNull
    @Column(name = "media_path")
    private String mediaPath;
}
