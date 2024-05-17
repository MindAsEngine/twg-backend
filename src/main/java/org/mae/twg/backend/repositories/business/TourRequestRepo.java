package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.business.TourRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourRequestRepo extends JpaRepository<TourRequest, Long> {

    @Query("""
            select r from TourRequest r
            where r.agency.id = :agencyId
                and r.agent is null
                and r.closedAt is null""")
    List<TourRequest> findOpenByAgency(@Param("agencyId") Long id, Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.agent is null
                and r.closedAt is null""")
    List<TourRequest> findOpen(Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.user.username = :name
                and r.closedAt is not null""")
    List<TourRequest> findClosedByUser(@Param("name") String username,
                                       Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.user.username = :name
                and r.closedAt is null""")
    List<TourRequest> findOpenByUser(@Param("name") String username,
                                     Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.agent.username = :name
                and r.closedAt is not null""")
    List<TourRequest> findClosedByAgent(@Param("name") String username,
                                        Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.agent.username = :name
                and r.closedAt is null""")
    List<TourRequest> findOpenByAgent(@Param("name") String username,
                                      Pageable pageable);
}
