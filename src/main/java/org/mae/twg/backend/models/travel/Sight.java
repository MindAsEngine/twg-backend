package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.travel.media.ResortMedia;
import org.mae.twg.backend.models.travel.media.SightMedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sights")
public class Sight {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sight_id")
    private Long id;

//    @Column(name = "slug", unique = true)
//    private String slug;
//    TODO: add slug generation

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @NonNull
    @Column(name = "address")
    private String address;

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "sights")
    private Set<Hotel> hotels = new HashSet<>();

    @OneToMany(mappedBy = "sight",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SightMedia> medias = new ArrayList<>();

    public void addMedia(SightMedia media) {
        medias.add(media);
        media.setSight(this);
    }

    public void removeMedia(SightMedia media) {
        medias.remove(media);
        media.setSight(null);
    }
}
