package org.mae.twg.backend.repositories.travel.images;

import org.mae.twg.backend.models.travel.media.SightMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SightMediaRepo extends JpaRepository<SightMedia, Long> {
    List<SightMedia> findBySight_id(Long id);
}
