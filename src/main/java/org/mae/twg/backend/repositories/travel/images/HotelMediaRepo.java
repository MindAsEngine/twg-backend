package org.mae.twg.backend.repositories.travel.images;

import org.mae.twg.backend.models.travel.media.HotelMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelMediaRepo extends JpaRepository<HotelMedia, Long> {
    List<HotelMedia> findByHotel_id(Long id);
}
