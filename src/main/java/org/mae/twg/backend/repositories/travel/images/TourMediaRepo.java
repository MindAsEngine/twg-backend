package org.mae.twg.backend.repositories.travel.images;

import org.mae.twg.backend.models.travel.media.TourMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourMediaRepo extends JpaRepository<TourMedia, Long> {
    List<TourMedia> findByTour_id(Long id);
}
