package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Resort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResortRepo extends JpaRepository<Resort, Long> {
    Optional<Resort> findBySlug(String slug);
}
