package org.mae.twg.backend.models.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.mae.twg.backend.models.auth.User;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "call_requests")
public class CallRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "call_request_id")
    private Long id;

    @NonNull
    @Column(name = "user_fio")
    private String user;

    @NonNull
    @Column(name = "number")
    private String number;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @NonNull
    @Column(name = "text")
    private String text;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public CallRequest(String user,
                       String number,
                       Agency agency,
                       String text) {
        this.user = user;
        this.number = number;
        this.agency = agency;
        this.text = text;
    }
}
