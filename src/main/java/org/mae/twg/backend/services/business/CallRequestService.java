package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.business.CallRequestDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.CallRequest;
import org.mae.twg.backend.repositories.business.AgencyRepo;
import org.mae.twg.backend.repositories.business.CallRequestRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallRequestService {
    private final CallRequestRepo callRequestRepo;
    private final AgencyRepo agencyRepo;
    private CallRequest findById(Long id) {
        CallRequest callRequest = callRequestRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("CallRequest with id=" + id + " not found"));
        return callRequest;
    }
    @Transactional
    public CallRequestDTO addRequest(CallRequestDTO callRequestDTO) {
        Agency agency = agencyRepo.findById(callRequestDTO.getAgency())
                .orElseThrow(() -> new ObjectNotFoundException("Agency with id=" + callRequestDTO.getAgency() + " not found"));
        CallRequest callRequest = new CallRequest(callRequestDTO.getUser(), callRequestDTO.getPhone(), agency, callRequestDTO.getText());
        callRequestRepo.saveAndFlush(callRequest);
        return new CallRequestDTO(callRequest);
    }

    public List<CallRequestDTO> getAll(Long id) {
        List<CallRequest> callRequests = callRequestRepo.findByAgency_idAndClosedAtIsNull(id);
        List<CallRequestDTO> callRequestDTOs = new ArrayList<>();

        for (CallRequest callRequest : callRequests) {
            callRequestDTOs.add(new CallRequestDTO(callRequest));
        }

        return callRequestDTOs;
    }

    public List<CallRequestDTO> resolve(Long request_id) {
        CallRequest callRequest = findById(request_id);
        callRequest.setClosedAt(LocalDateTime.now());
        callRequestRepo.saveAndFlush(callRequest);
        return getAll(callRequest.getAgency().getId());
    }
}
