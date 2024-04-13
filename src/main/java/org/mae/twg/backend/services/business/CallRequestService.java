package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.business.CallReqResponseDTO;
import org.mae.twg.backend.dto.business.CallRequestDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.CallRequest;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.CallRequestRepo;
import org.mae.twg.backend.services.auth.UserService;
import org.mae.twg.backend.utils.BotUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class CallRequestService {
    private final CallRequestRepo callRequestRepo;
    private final AgencyService agencyService;
    private final UserService userService;
    private final BotUtils botUtils;
    private CallRequest findById(Long id) {
        log.debug("Start CallRequestService.findById");
        CallRequest callRequest = callRequestRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("CallRequest with id=" + id + " not found");
                    return new ObjectNotFoundException("CallRequest with id=" + id + " not found");
                });
        log.debug("End CallRequestService.findById");
        return callRequest;
    }
    private List<CallReqResponseDTO> modelsToDTOs(Stream<CallRequest> callRequests, Localization localization) {
        log.debug("Start CallRequestService.modelsToDTOs");
        return callRequests
                .map(callRequest -> new CallReqResponseDTO(callRequest, localization))
                .collect(Collectors.toList());
    }
    @Transactional
    public CallReqResponseDTO addRequest(CallRequestDTO callRequestDTO, Localization localization) {
        log.debug("Start CallRequestService.addRequest");
        Agency agency = agencyService.findById(callRequestDTO.getAgencyId());
        CallRequest callRequest = new CallRequest(callRequestDTO.getFullName(), callRequestDTO.getPhone(), agency, callRequestDTO.getText());
        callRequestRepo.saveAndFlush(callRequest);
        botUtils.sendCallNotifications();
        log.debug("End CallRequestService.addRequest");
        return new CallReqResponseDTO(callRequest, localization);
    }

    public List<CallReqResponseDTO> getAll(Long id, String username, Localization localization) {
        log.debug("Start CallRequestService.getAll");
        if (id != null) {
            log.debug("End CallRequestService.getAll");
            return modelsToDTOs(callRequestRepo.findByAgency_idAndClosedAtIsNull(id).stream(), localization);
        }
        if (username != null) {
            log.debug("End CallRequestService.getAll");
            return modelsToDTOs(callRequestRepo.findByAgent_UsernameAndClosedAtIsNull(username).stream(), localization);
        }
        log.debug("End CallRequestService.getAll");
        return modelsToDTOs(callRequestRepo.findByAgentIsNullAndClosedAtIsNull().stream(), localization);
    }

    public CallReqResponseDTO setAgent(Long requestId, Localization localization) {
        log.debug("Start CallRequestService.setAgent");
        CallRequest callRequest = findById(requestId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User agent = userService.loadUserByUsername(username);
        callRequest.setAgent(agent);
        callRequestRepo.saveAndFlush(callRequest);
        log.debug("End CallRequestService.setAgent");
        return new CallReqResponseDTO(callRequest, localization);
    }

    public List<CallReqResponseDTO> resolve(Long request_id, Localization localization) {
        log.debug("Start CallRequestService.resolve");
        CallRequest callRequest = findById(request_id);
        callRequest.setClosedAt(LocalDateTime.now());
        callRequestRepo.saveAndFlush(callRequest);
        log.debug("End CallRequestService.resolve");
        return getAll(null, null, localization);
    }
}
