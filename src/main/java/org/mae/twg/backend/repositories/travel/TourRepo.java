package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourRepo extends JpaRepository<Tour, Long> {
    Optional<Tour> findBySlug(String slug);
}
