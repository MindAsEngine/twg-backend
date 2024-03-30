package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.comments.HospitalComment;
import org.mae.twg.backend.models.travel.localization.HospitalLocal;
import org.mae.twg.backend.models.travel.media.HospitalMedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospitals")
public class Hospital implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hospital_id")
    private Long id;
    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;
    @Column(name = "slug", unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_img_id")
    private HospitalMedia header;

    @OneToMany(mappedBy = "hospital",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HospitalLocal> locals = new ArrayList<>();
    @OneToMany(mappedBy = "hospital",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HospitalComment> comments = new ArrayList<>();
    private Double latitude;
    private Double longitude;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hospital")
    private Set<Tour> tours = new HashSet<>();
    @OneToMany(mappedBy = "hospital",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<HospitalMedia> medias = new ArrayList<>();

    public void addComment(HospitalComment comment) {
        comments.add(comment);
        comment.setHospital(this);
    }

    public void removeComment(HospitalComment comment) {
        comments.remove(comment);
        comment.setHospital(null);
    }

    public void addLocal(HospitalLocal local) {
        locals.add(local);
        local.setHospital(this);
    }

    public void removeLocal(HospitalLocal local) {
        locals.remove(local);
        local.setHospital(null);
    }

    public void addHeader(HospitalMedia media) {
        header = media;
        media.setHospital(this);
    }

    public void removeHeader(HospitalMedia media) {
        header = null;
        media.setHospital(null);
    }

    public void addMedia(HospitalMedia media) {
        medias.add(media);
        media.setHospital(this);
    }

    public void removeMedia(HospitalMedia media) {
        medias.remove(media);
        media.setHospital(null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<HospitalLocal> getLocalizations() {
        return locals;
    }
}