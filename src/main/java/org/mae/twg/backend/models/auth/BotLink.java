package org.mae.twg.backend.models.auth;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bot_links")
public class BotLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
