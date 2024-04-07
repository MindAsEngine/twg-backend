package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.business.TourReqResponseDTO;
import org.mae.twg.backend.dto.business.TourRequestDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.TourRequestRepo;
import org.mae.twg.backend.services.travel.TourService;
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
public class TourRequestService {
    private final TourRequestRepo tourRequestRepo;
    private final TourService tourService;
    private final AgencyService agencyService;
    private final BotUtils botUtils;

    private TourRequest findById(Long id) {
        log.debug("Start TourRequestService.findById");
        TourRequest tourRequest = tourRequestRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("TourRequest with id=" + id + " not found");
                    return new ObjectNotFoundException("TourRequest with id=" + id + " not found");
                });
        log.debug("End TourRequestService.findById");
        return tourRequest;
    }
    private List<TourReqResponseDTO> modelsToDTOs(Stream<TourRequest> tourRequests, Localization localization) {
        log.debug("Start TourRequestService.modelsToDTOs");
        return tourRequests
                .map(tourRequest -> new TourReqResponseDTO(tourRequest, localization))
                .collect(Collectors.toList());
    }
    @Transactional
    public TourReqResponseDTO addRequest(TourRequestDTO tourRequestDTO, Localization localization) {
        log.debug("Start TourRequestService.addRequest");
        Tour tour = tourService.findById(tourRequestDTO.getTourId());
        Agency agency = agencyService.findById(tourRequestDTO.getAgencyId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TourRequest tourRequest = new TourRequest(user, agency, tour, tourRequestDTO.getAdults(), tourRequestDTO.getChildren(), tourRequestDTO.getTransferNotes());
        tourRequestRepo.saveAndFlush(tourRequest);

        botUtils.sendNotifications();
        log.debug("End TourRequestService.addRequest");
        return new TourReqResponseDTO(tourRequest, localization);
    }

    public List<TourReqResponseDTO> getAll(Long agencyId, String username, Localization localization) {
        log.debug("Start TourRequestService.getAll");
        if (agencyId != null) {
            log.debug("End TourRequestService.getAll");
            return modelsToDTOs(tourRequestRepo.findByAgency_IdAndClosedAtIsNull(agencyId).stream(), localization);
        }
        if (username != null) {
            log.debug("End TourRequestService.getAll");
            return modelsToDTOs((tourRequestRepo.findAllByUser_Username(username)).stream(), localization);
        }
        log.debug("End TourRequestService.getAll");
        return modelsToDTOs(tourRequestRepo.findByClosedAtIsNull().stream(), localization);
    }

    public List<TourReqResponseDTO> resolve(Long request_id, Localization localization) {
        log.debug("Start TourRequestService.resolve");
        TourRequest tourRequest = findById(request_id);
        tourRequest.setClosedAt(LocalDateTime.now());
        tourRequestRepo.saveAndFlush(tourRequest);
        log.debug("End TourRequestService.resolve");
        return getAll(null, null, localization);
    }


}
