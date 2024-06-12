package org.mae.twg.backend.repositories.auth;

import org.mae.twg.backend.models.auth.BotLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotLinkRepo extends JpaRepository<BotLink, String> {
    Optional<BotLink> findByUser_Username(String botId);
}
