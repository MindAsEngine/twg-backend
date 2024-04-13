package org.mae.twg.backend.models.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.Tour;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tour_requests")
public class TourRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tour_request_id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private Tour tour;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    private Integer adults;

    private Integer children;

    @Column(name = "transfer_notes",
            columnDefinition = "TEXT")
    private String transferNotes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public TourRequest(User user,
                       Agency agency,
                       Tour tour,
                       Integer adults,
                       Integer children,
                       String transferNotes) {
        this.user = user;
        this.agency = agency;
        this.tour = tour;
        this.adults = adults;
        this.children = children;
        this.transferNotes = transferNotes;
    }
}
