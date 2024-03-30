package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.business.CallReqResponseDTO;
import org.mae.twg.backend.dto.business.CallRequestDTO;
import org.mae.twg.backend.dto.business.TourReqResponseDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.CallRequest;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.AgencyRepo;
import org.mae.twg.backend.repositories.business.CallRequestRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CallRequestService {
    private final CallRequestRepo callRequestRepo;
    private final AgencyService agencyService;
    private CallRequest findById(Long id) {
        CallRequest callRequest = callRequestRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("CallRequest with id=" + id + " not found"));
        return callRequest;
    }
    private List<CallReqResponseDTO> modelsToDTOs(Stream<CallRequest> callRequests, Localization localization) {
        return callRequests
                .map(callRequest -> new CallReqResponseDTO(callRequest, localization))
                .collect(Collectors.toList());
    }
    @Transactional
    public CallReqResponseDTO addRequest(CallRequestDTO callRequestDTO, Localization localization) {
        Agency agency = agencyService.findById(callRequestDTO.getAgencyId());
        CallRequest callRequest = new CallRequest(callRequestDTO.getFullName(), callRequestDTO.getPhone(), agency, callRequestDTO.getText());
        callRequestRepo.saveAndFlush(callRequest);
        return new CallReqResponseDTO(callRequest, localization);
    }

    public List<CallReqResponseDTO> getAll(Long id, Localization localization) {
        List<CallRequest> callRequests = callRequestRepo.findByAgency_idAndClosedAtIsNull(id);
        if (id == null) {
            callRequests = callRequestRepo.findByClosedAtIsNull();
        }
        return modelsToDTOs(callRequests.stream(), localization);
    }

    public List<CallReqResponseDTO> resolve(Long request_id, Localization localization) {
        CallRequest callRequest = findById(request_id);
        callRequest.setClosedAt(LocalDateTime.now());
        callRequestRepo.saveAndFlush(callRequest);
        return getAll(null, localization);
    }
}
