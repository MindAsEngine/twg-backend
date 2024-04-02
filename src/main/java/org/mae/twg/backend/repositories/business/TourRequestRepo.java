package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.business.TourRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRequestRepo extends JpaRepository<TourRequest, Long> {
    List<TourRequest> findByAgency_IdAndClosedAtIsNull(Long id);
    List<TourRequest> findByClosedAtIsNull();

    List<TourRequest> findAllByUser_Username(String username);
}
