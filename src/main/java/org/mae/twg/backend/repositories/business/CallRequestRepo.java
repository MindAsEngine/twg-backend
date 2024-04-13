package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.business.CallRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallRequestRepo extends JpaRepository<CallRequest, Long> {
    List<CallRequest> findByAgency_idAndClosedAtIsNull(Long id);
    List<CallRequest> findByAgent_UsernameAndClosedAtIsNull(String username);
    List<CallRequest> findByAgentIsNullAndClosedAtIsNull();
}
