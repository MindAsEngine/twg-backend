package org.mae.twg.backend.repositories.travel;

import org.mae.twg.backend.models.travel.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepo extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findBySlug(String slug);
}
