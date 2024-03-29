package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.comments.TourComment;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;
import org.mae.twg.backend.models.travel.media.TourMedia;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tours")
public class Tour implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tour_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "route",
            columnDefinition = "TEXT")
    private String route;

    @Column(name = "price")
    private Long price;

    @OneToMany(mappedBy = "tour",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TourLocal> locals = new ArrayList<>();

    @OneToMany(mappedBy = "tour",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TourComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "tour",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TourPeriod> periods = new ArrayList<>();

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TourType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Sight hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_img_id")
    private TourMedia header;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "illustration_img_id")
    private TourMedia illustration;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tour_hotels",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_id"))
    private Set<Hotel> hotels = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tour_tags",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "duration")
    private Integer duration;

    @NonNull
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "tour",
            cascade = CascadeType.ALL)
    private List<TourMedia> medias = new ArrayList<>();

    public void addComment(TourComment comment) {
        comments.add(comment);
        comment.setTour(this);
    }

    public void removeComment(TourComment comment) {
        comments.remove(comment);
        comment.setTour(null);
    }

    public void addPeriod(TourPeriod period) {
        periods.add(period);
        period.setTour(this);
    }

    public void removePeriod(TourPeriod period) {
        periods.remove(period);
        period.setTour(null);
    }

    public void addLocal(TourLocal local) {
        locals.add(local);
        local.setTour(this);
    }

    public void removeLocal(TourLocal local) {
        locals.remove(local);
        local.setTour(null);
    }
    public void addHeader(TourMedia media) {
        header = media;
        media.setTour(this);
    }
    public void removeHeader(TourMedia media) {
        header = null;
        media.setTour(null);
    }
    public void addMedia(TourMedia media) {
        medias.add(media);
        media.setTour(this);
    }

    public void removeMedia(TourMedia media) {
        medias.remove(media);
        media.setTour(null);
    }

    public void addHotel(Hotel hotel) {
        hotels.add(hotel);
        hotel.getTours().add(this);
    }

    public void removeHotel(Hotel hotel) {
        hotels.remove(hotel);
        hotel.getTours().remove(this);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getTours().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getTours().remove(this);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<TourLocal> getLocalizations() {
        return locals;
    }
}
