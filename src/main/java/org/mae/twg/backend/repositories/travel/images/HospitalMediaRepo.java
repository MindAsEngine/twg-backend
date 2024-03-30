package org.mae.twg.backend.repositories.travel.images;

import org.mae.twg.backend.models.travel.media.HospitalMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalMediaRepo extends JpaRepository<HospitalMedia, Long> {
    List<HospitalMedia> findByHospital_id(Long id);
}
