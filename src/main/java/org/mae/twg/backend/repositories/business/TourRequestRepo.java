package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.business.TourRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourRequestRepo extends JpaRepository<TourRequest, Long> {

    @Query("""
            select r from TourRequest r
            where r.agency.id = :agencyId
                and r.agent is null
                and r.closedAt is null""")
    Page<TourRequest> findOpenByAgency(@Param("agencyId") Long id, Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.agent is null
                and r.closedAt is null""")
    Page<TourRequest> findOpen(Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.user.username = :name
                and r.closedAt is not null""")
    Page<TourRequest> findClosedByUser(@Param("name") String username,
                                       Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.user.username = :name
                and r.closedAt is null""")
    Page<TourRequest> findOpenByUser(@Param("name") String username,
                                     Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.agent.username = :name
                and r.closedAt is not null""")
    Page<TourRequest> findClosedByAgent(@Param("name") String username,
                                        Pageable pageable);

    @Query("""
            select r from TourRequest r
            where r.agent.username = :name
                and r.closedAt is null""")
    Page<TourRequest> findOpenByAgent(@Param("name") String username,
                                      Pageable pageable);
}
