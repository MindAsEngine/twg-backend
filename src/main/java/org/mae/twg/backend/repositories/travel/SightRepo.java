package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Sight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SightRepo extends JpaRepository<Sight, Long> {
    Optional<Sight> findBySlug(String slug);
}
